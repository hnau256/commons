package org.hnau.commons.gen.fold.processor.info.builder

import arrow.core.toNonEmptyListOrNull
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeVariableName
import org.hnau.commons.gen.fold.processor.AnnotationInfo
import org.hnau.commons.gen.fold.processor.info.FoldInfo
import org.hnau.commons.gen.kotlin.arguments
import org.hnau.commons.kotlin.foldBoolean
import org.hnau.commons.kotlin.ifNull
import org.hnau.commons.kotlin.ifTrue
import com.squareup.kotlinpoet.ksp.toTypeName as kspToTypeName

private val nonPublicModifiers: Set<Modifier> = setOf(
    Modifier.PRIVATE,
    Modifier.PROTECTED,
    Modifier.INTERNAL,
)

fun FoldInfo.Companion.create(
    logger: KSPLogger,
    annotated: KSAnnotated,
): FoldInfo? {

    if (!annotated.validate()) {
        logger.error("Is not valid", annotated)
        return null
    }

    val classDeclaration = (annotated as? KSClassDeclaration).ifNull {
        logger.error("Is not class", annotated)
        return null
    }

    val isEnum = classDeclaration.classKind == ClassKind.ENUM_CLASS
    val isSealed = classDeclaration.classKind in setOf(ClassKind.CLASS, ClassKind.INTERFACE)
            && classDeclaration.modifiers.contains(Modifier.SEALED)

    if (!isEnum && !isSealed) {
        logger.error("Must be enum class, sealed class, or sealed interface", classDeclaration)
        return null
    }

    val foldAnnotation = classDeclaration
        .annotations
        .firstOrNull { it.shortName.asString() == AnnotationInfo.simpleName }
        .ifNull {
            logger.error("Unable find @${AnnotationInfo.simpleName} annotation", classDeclaration)
            return null
        }

    val hasCompanionObject = classDeclaration
        .declarations
        .filterIsInstance<KSClassDeclaration>()
        .any { it.isCompanionObject }

    val nonEmptyEntries = foldAnnotation
        .arguments(logger)
        .get<Boolean>(
            name = "nonEmptyEntries",
            onNoArgument = { false },
        )
        ?: return null

    if (nonEmptyEntries && !isEnum) {
        logger.error(
            "@${AnnotationInfo.simpleName}(nonEmptyEntries = true) can be used only with enum classes",
            classDeclaration,
        )
        return null
    }

    val dispatchTypeParameterName = foldAnnotation
        .arguments(logger)
        .get<String>(
            name = "dispatchTypeParameter",
            onNoArgument = { "" },
        )
        ?: return null

    dispatchTypeParameterName.isNotEmpty().ifTrue {
        isEnum.ifTrue {
            logger.error(
                "@${AnnotationInfo.simpleName}(dispatchTypeParameter = ...) is not supported for enum classes",
                classDeclaration,
            )
            return null
        }
    }

    if (nonEmptyEntries && !hasCompanionObject) {
        logger.error(
            "@${AnnotationInfo.simpleName}(nonEmptyEntries = true) requires enum class to have a companion object",
            classDeclaration,
        )
        return null
    }

    val typeParamResolver: TypeParameterResolver =
        classDeclaration.typeParameters.toTypeParameterResolver()

    val typeVariables = classDeclaration.typeParameters
        .map { typeParameter ->
            val variable = typeParameter.toTypeVariableName(typeParamResolver)
            TypeVariableName(variable.name, variable.bounds)
        }

    val dispatchTypeParameterIndex: Int? = dispatchTypeParameterName
        .takeIf(String::isNotEmpty)
        ?.let { name ->
            typeVariables
                .indexOfFirst { typeVariable -> typeVariable.name == name }
                .takeIf { index -> index >= 0 }
                .ifNull {
                    logger.error(
                        "@${AnnotationInfo.simpleName}(dispatchTypeParameter = \"$name\") type parameter not found",
                        classDeclaration,
                    )
                    return null
                }
        }

    fun KSTypeReference.toTypeName(): TypeName {
        val typeName = kspToTypeName(typeParamResolver)
        val annotationSpecs = annotations
            .mapNotNull { runCatching { it.toAnnotationSpec() }.getOrNull() }
            .toList()
        if (annotationSpecs.isEmpty()) return typeName
        return typeName.copy(false, annotationSpecs, emptyMap())
    }

    val dispatchResultTypes: Map<String, TypeName> = dispatchTypeParameterIndex?.let { dispatchTypeParameterIndex ->
        run {
            val parentName = classDeclaration.qualifiedName?.asString()
            classDeclaration
                .getSealedSubclasses()
                .associate { subclass ->
                    val subclassName = subclass.qualifiedName?.asString().orEmpty()
                    val superType = subclass
                        .superTypes
                        .map { it.resolve() }
                        .firstOrNull { type ->
                            type.declaration.qualifiedName?.asString() == parentName
                        }
                    val argumentReference = superType
                        ?.arguments
                        ?.getOrNull(dispatchTypeParameterIndex)
                        ?.type
                    val argumentType = argumentReference?.resolve()

                    when {
                        argumentReference == null || argumentType == null || argumentType.isError -> {
                            logger.error(
                                "Unable to resolve dispatch result type of variant $subclassName",
                                subclass,
                            )
                            return null
                        }

                        argumentType.declaration is KSTypeParameter -> {
                            logger.error(
                                "@${AnnotationInfo.simpleName}(dispatchTypeParameter = ...) does not support generic variants: $subclassName",
                                subclass,
                            )
                            return null
                        }

                        else -> subclassName to argumentReference.toTypeName()
                    }
                }
        }
    } ?: emptyMap()

    val variantList = isEnum.foldBoolean(
        ifTrue = {
            val enumClassName = classDeclaration.toClassName()
            classDeclaration.declarations
                .filterIsInstance<KSClassDeclaration>()
                .filter { it.classKind == ClassKind.ENUM_ENTRY }
                .map { entry ->
                    FoldInfo.Variant(
                        identifier = entry.simpleName.asString(),
                        resolution = FoldInfo.Resolution.Object,
                        className = enumClassName,
                    )
                }
                .toList()
        },
        ifFalse = {
            classDeclaration
                .getSealedSubclasses()
                .map { subclass ->
                    val identifier = subclass.simpleName.asString()
                    val subclassClassName = subclass.toClassName()
                    val subclassTypeVariableNames = subclass
                        .typeParameters
                        .map { it.simpleName.asString() }
                        .toSet()
                    val resolution = when {
                        subclass.classKind == ClassKind.OBJECT ->
                            FoldInfo.Resolution.Object

                        subclass.modifiers.contains(Modifier.DATA) -> {

                            val publicProperties = subclass
                                .getAllProperties()
                                .filter { property ->
                                    property
                                        .modifiers
                                        .none { it in nonPublicModifiers }
                                }
                                .associateBy { it.simpleName.asString() }

                            FoldInfo.Resolution.Destructured(
                                parameters = subclass
                                    .primaryConstructor
                                    ?.parameters
                                    .orEmpty()
                                    .mapNotNull { param ->

                                        val name = param
                                            .name
                                            ?.asString()
                                            ?.takeIf { it in publicProperties }
                                            ?: return@mapNotNull null

                                        FoldInfo.Parameter(
                                            name = name,
                                            type = param.type.toTypeName(),
                                        )
                                    },
                            )
                        }

                        else -> FoldInfo.Resolution.Whole(
                            type = subclass.toClassName(),
                        )
                    }
                    val dispatchResultType = dispatchResultTypes[subclass.qualifiedName?.asString()]

                    FoldInfo.Variant(
                        identifier = identifier,
                        resolution = resolution,
                        className = subclassClassName,
                        typeVariables = typeVariables.filter { typeVariable ->
                            typeVariable.name in subclassTypeVariableNames
                        },
                        dispatchResultType = dispatchResultType,
                    )
                }
                .toList()
        }
    )

    return variantList
        .toNonEmptyListOrNull()
        .ifNull {
            logger.error("No variants found", classDeclaration)
            null
        }
        ?.let { variants ->
            FoldInfo(
                classDeclaration = classDeclaration,
                typeVariables = typeVariables,
                variants = variants,
                nonEmptyEntries = nonEmptyEntries,
                dispatchTypeParameterIndex = dispatchTypeParameterIndex,
            )
        }
}

