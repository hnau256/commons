package org.hnau.commons.gen.sealup.processor.sealedinfo.builder

import arrow.core.Either
import arrow.core.toNonEmptyListOrNull
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.validate
import org.hnau.commons.gen.kotlin.arguments
import org.hnau.commons.gen.kotlin.nameWithoutPackage
import org.hnau.commons.gen.sealup.processor.AnnotationInfo
import org.hnau.commons.gen.sealup.processor.sealedinfo.CreateResult
import org.hnau.commons.gen.sealup.processor.sealedinfo.SealedInfo
import org.hnau.commons.kotlin.ifNull
import org.hnau.commons.kotlin.ifTrue
import org.hnau.commons.kotlin.lazy.Initializer

fun SealedInfo.Companion.create(
    logger: KSPLogger,
    annotated: KSAnnotated,
): CreateResult<SealedInfo> {

    // Defer if the symbol has unresolved types.
    // Multi-round processing will retry when dependencies are generated.
    if (!annotated.validate()) {
        return CreateResult.Deferred
    }

    val classDeclaration = (annotated as? KSClassDeclaration).ifNull {
        logger.error("Is not class", annotated)
        return CreateResult.Error
    }

    if (classDeclaration.classKind != ClassKind.INTERFACE) {
        logger.error("Is not interface", classDeclaration)
        return CreateResult.Error
    }

    if (classDeclaration.typeParameters.isNotEmpty()) {
        logger.error("Unable to seal up interface with type parameters", classDeclaration)
        return CreateResult.Error
    }

    val sealUpAnnotation = classDeclaration
        .annotations
        .firstOrNull { it.shortName.asString() == AnnotationInfo.simpleName }
        .ifNull {
            logger.error("Unable find @${AnnotationInfo.simpleName} annotation", classDeclaration)
            return CreateResult.Error
        }

    val arguments = sealUpAnnotation.arguments(logger)

    val wrappedValuePropertyName = arguments
        .get<String>(
            name = "wrappedValuePropertyName",
            onNoArgument = { "value" },
        )
        ?: return CreateResult.Error

    val parentExtension = Initializer<SealedInfo.ParentExtension?>()

    val getParentExtension: (
        target: String,
    ) -> SealedInfo.ParentExtension? = { target ->
        parentExtension {
            classDeclaration
                .declarations
                .filterIsInstance<KSClassDeclaration>()
                .firstOrNull { it.isCompanionObject }
                .ifNull {
                    logger.error(
                        "Companion object of sealing up interface is expected for generating $target",
                        classDeclaration
                    )
                    null
                }
                ?.let(SealedInfo::ParentExtension)
        }
    }

    val factoryMethods = arguments
        .get<Boolean>("factoryMethods") { true }
        .ifNull { return CreateResult.Error }
        .ifTrue { getParentExtension("factory methods") ?: return CreateResult.Error }

    val variants = arguments
        .get<List<KSAnnotation>>("variants")
        .ifNull { return CreateResult.Error }
        .toNonEmptyListOrNull()
        .ifNull {
            logger.error("Expected at least one variant", sealUpAnnotation)
            return CreateResult.Error
        }
        .map { annotation ->
            when (
                val result = SealedInfo.Variant.create(
                    logger = logger,
                    annotation = annotation,
                    wrappedValuePropertyName = wrappedValuePropertyName,
                    collectConstructors = factoryMethods != null,
                )
            ) {
                is CreateResult.Success -> result.value
                is CreateResult.Deferred -> return CreateResult.Deferred
                is CreateResult.Error -> return CreateResult.Error
            }
        }

    val overrides = classDeclaration
        .declarations
        .mapNotNull { declaration ->
            when (declaration) {
                is KSFunctionDeclaration -> Either.Right(declaration)
                is KSPropertyDeclaration -> Either.Left(declaration)
                else -> null
            }
        }
        .toList()
        .map { declaration: Either<KSPropertyDeclaration, KSFunctionDeclaration> ->
            SealedInfo.Override
                .create(
                    logger = logger,
                    declaration = declaration,
                )
                .ifNull { return CreateResult.Error }
        }

    val sealedInterfaceName = arguments
        .get<String>(
            name = "sealedInterfaceName",
            onNoArgument = {
                classDeclaration
                    .nameWithoutPackage(logger)
                    .ifNull { return CreateResult.Error }
                    .replace(".", "")
                    .plus("Sealed")
            }
        )
        .ifNull { return CreateResult.Error }

    return CreateResult.Success(
        SealedInfo(
            parent = classDeclaration,
            variants = variants,
            serializable = arguments.get<Boolean>("serializable") { false }
                ?: return CreateResult.Error,
            ordinal = arguments.get<Boolean>("ordinal") { true }
                ?: return CreateResult.Error,
            name = arguments.get<Boolean>("name") { false }
                ?: return CreateResult.Error,
            sealedInterfaceName = sealedInterfaceName,
            fold = arguments.get<Boolean>("fold") { true }
                ?: return CreateResult.Error,
            overrides = overrides,
            factoryMethods = factoryMethods,
        )
    )
}