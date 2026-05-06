package org.hnau.commons.gen.sealup.processor.sealedinfo.builder

import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import org.hnau.commons.gen.kotlin.arguments
import org.hnau.commons.gen.kotlin.resolve
import org.hnau.commons.gen.kotlin.stickedName
import org.hnau.commons.gen.sealup.processor.sealedinfo.CreateResult
import org.hnau.commons.gen.sealup.processor.sealedinfo.SealedInfo
import org.hnau.commons.kotlin.castOrNull
import org.hnau.commons.kotlin.ifNull

fun SealedInfo.Variant.Companion.create(
    index: Int,
    logger: KSPLogger,
    annotation: KSAnnotation,
    wrappedValuePropertyName: String,
    collectConstructors: Boolean,
): CreateResult<SealedInfo.Variant> {
    val arguments = annotation.arguments(logger)

    val wrapped = arguments
        .get<KSType>("type")
        .ifNull { return CreateResult.Error }
        .apply {
            logger.info("Type qualified name: ${declaration.qualifiedName?.asString()}")
        }
        .takeIf { it.declaration.qualifiedName?.asString() !in noWrappedMarkerClasses }
        ?.let { type ->

            if (type.containsErrorType()) {
                return CreateResult.Deferred
            }

            val classDeclaration = type
                .declaration
                .let { declaration ->
                    declaration
                        .castOrNull<KSClassDeclaration>()
                        .ifNull {
                            logger.error("Class expected", declaration)
                            return CreateResult.Error
                        }
                }

            SealedInfo.Variant.Wrapped(
                type = type,
                pointer = when (val classKind = classDeclaration.classKind) {
                    ClassKind.OBJECT -> SealedInfo.Variant.Wrapped.Pointer.Object
                    ClassKind.CLASS -> SealedInfo.Variant.Wrapped.Pointer.Class(
                        property = arguments
                            .get<String>("wrappedValuePropertyName") { wrappedValuePropertyName }
                            .ifNull { return CreateResult.Error },
                        constructors = classDeclaration
                            .takeIf { collectConstructors }
                            ?.takeUnless(KSClassDeclaration::isAbstract)
                            ?.getConstructors()
                            ?.filter(KSFunctionDeclaration::isPublic)
                            ?.toList()
                            ?.flatMap { constructor ->
                                val parameters = constructor
                                    .parameters
                                    .map { ksParameter ->
                                        val paramType = ksParameter
                                            .type
                                            .resolve(logger)
                                            .ifNull { return CreateResult.Error }

                                        if (paramType.containsErrorType()) {
                                            return CreateResult.Deferred
                                        }

                                        val parameter = SealedInfo.Variant.Constructor.Parameter(
                                            name = ksParameter.name?.asString(),
                                            type = paramType,
                                        )
                                        parameter to ksParameter.hasDefault
                                    }

                                listOf(false, true)
                                    .map { skipWithDefaults ->
                                        parameters.mapNotNull { (parameter, hasDefault) ->
                                            if (hasDefault && skipWithDefaults) {
                                                return@mapNotNull null
                                            }
                                            parameter
                                        }
                                    }
                                    .distinct()
                                    .map(SealedInfo.Variant::Constructor)
                            }
                            .orEmpty(),
                    )

                    else -> {
                        logger.error("Unsupported class kind $classKind", classDeclaration)
                        return CreateResult.Error
                    }
                },
            )
        }

    val wrapperIdentifier = arguments
        .get<String>("identifier") {
            wrapped
                ?.type
                ?.declaration
                ?.stickedName(logger)
                ?.replaceFirstChar(Char::lowercase)
                ?: "variant$index"
        }
        .ifNull { return CreateResult.Error }

    return CreateResult.Success(
        SealedInfo.Variant(
            wrapped = wrapped,
            wrapperClass = arguments
                .get<String>("wrapperClassName") { wrapperIdentifier.replaceFirstChar(Char::uppercase) }
                .ifNull { return CreateResult.Error },
            wrapperIdentifier = wrapperIdentifier,
            serialName = arguments
                .get<String>("serialName") { wrapperIdentifier }
                .ifNull { return CreateResult.Error },
        ),
    )
}

/**
 * Recursively checks if a [KSType] contains any unresolved (error) types.
 */
private fun KSType.containsErrorType(): Boolean {
    if (isError) return true
    return arguments.any { arg ->
        arg.type?.resolve()?.containsErrorType() == true
    }
}

private val noWrappedMarkerClasses: Set<String> = listOf(
    Unit::class,
    Nothing::class,
)
    .map { it.qualifiedName!! }
    .toSet()
