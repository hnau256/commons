package org.hnau.commons.gen.fold.processor.info.builder

import arrow.core.toNonEmptyListOrNull
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
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
import org.hnau.commons.gen.fold.processor.info.FoldInfo
import org.hnau.commons.kotlin.foldBoolean
import org.hnau.commons.kotlin.ifNull
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

    val typeParamResolver: TypeParameterResolver =
        classDeclaration.typeParameters.toTypeParameterResolver()

    val typeVariables = classDeclaration.typeParameters
        .map { typeParameter ->
            val variable = typeParameter.toTypeVariableName(typeParamResolver)
            TypeVariableName(variable.name, variable.bounds)
        }

    fun KSTypeReference.toTypeName(): TypeName {
        val typeName = kspToTypeName(typeParamResolver)
        val annotationSpecs = annotations
            .mapNotNull { runCatching { it.toAnnotationSpec() }.getOrNull() }
            .toList()
        if (annotationSpecs.isEmpty()) return typeName
        return typeName.copy(false, annotationSpecs, emptyMap())
    }

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
                    FoldInfo.Variant(
                        identifier = identifier,
                        resolution = resolution,
                        className = subclassClassName,
                        typeVariables = typeVariables.filter { typeVariable ->
                            typeVariable.name in subclassTypeVariableNames
                        },
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
            )
        }
}

