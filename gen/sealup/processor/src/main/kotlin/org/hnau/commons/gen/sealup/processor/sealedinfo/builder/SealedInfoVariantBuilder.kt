package org.hnau.commons.gen.sealup.processor.sealedinfo.builder

import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import org.hnau.commons.gen.kotlin.arguments
import org.hnau.commons.gen.kotlin.resolve
import org.hnau.commons.gen.kotlin.stickedName
import org.hnau.commons.gen.sealup.processor.sealedinfo.CreateResult
import org.hnau.commons.gen.sealup.processor.sealedinfo.SealedInfo
import org.hnau.commons.kotlin.castOrNull
import org.hnau.commons.kotlin.foldBoolean
import org.hnau.commons.kotlin.ifNull

fun SealedInfo.Variant.Companion.create(
    logger: KSPLogger,
    annotation: KSAnnotation,
    wrappedValuePropertyName: String,
    collectConstructors: Boolean,
): CreateResult<SealedInfo.Variant> {
    val arguments = annotation.arguments(logger)

    val type = arguments
        .get<KSType>("type")
        .ifNull { return CreateResult.Error }

    // Check if the variant type itself contains error types
    if (type.containsErrorType()) {
        return CreateResult.Deferred
    }

    val declaration = type
        .declaration
        .ifNull {
            logger.error("Type '$type' has no declaration")
            return CreateResult.Error
        }

    val classDeclaration = declaration
        .castOrNull<KSClassDeclaration>()
        .ifNull {
            logger.error("Class expected", declaration)
            return CreateResult.Error
        }

    val wrapped = classDeclaration
        .qualifiedName
        ?.asString()
        .equals("kotlin.Nothing")
        .foldBoolean(
            ifTrue = { SealedInfo.Variant.Wrapped.None },
            ifFalse = {

                val mode: SealedInfo.Variant.Wrapped.Some.Mode = classDeclaration
                    .classKind
                    .let { it == ClassKind.OBJECT }
                    .foldBoolean(
                        ifTrue = {
                            SealedInfo.Variant.Wrapped.Some.Mode.Object(
                                identifier = classDeclaration,
                            )
                        },
                        ifFalse = {
                            SealedInfo.Variant.Wrapped.Some.Mode.Class(
                                identifier = arguments
                                    .get<String>("wrappedValuePropertyName") { wrappedValuePropertyName }
                                    .ifNull { return CreateResult.Error },
                                constructors = classDeclaration
                                    .takeIf { collectConstructors }
                                    ?.getConstructors()
                                    ?.toList()
                                    .orEmpty()
                                    .flatMap { constructor ->
                                        val parameters = constructor
                                            .parameters
                                            .map { ksParameter ->
                                                val paramType = ksParameter
                                                    .type
                                                    .resolve(logger)
                                                    .ifNull { return CreateResult.Error }

                                                // Check if parameter type contains error types
                                                if (paramType.containsErrorType()) {
                                                    return CreateResult.Deferred
                                                }

                                                val parameter =
                                                    SealedInfo.Variant.Wrapped.Some.Mode.Class.Constructor.Parameter(
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
                                            .map(SealedInfo.Variant.Wrapped.Some.Mode.Class::Constructor)
                                    }
                            )
                        }
                    )

                SealedInfo.Variant.Wrapped.Some(
                    type = type,
                    mode = mode,
                )
            }
        )

    val stickedName = classDeclaration
        .stickedName(logger)
        ?: return CreateResult.Error

    val identifier = arguments
        .get<String>("identifier") { stickedName.replaceFirstChar(Char::lowercase) }
        .ifNull { return CreateResult.Error }

    return CreateResult.Success(
        SealedInfo.Variant(
            wrapped = wrapped,
            wrapperClass = arguments
                .get<String>("wrapperClassName") { identifier.replaceFirstChar(Char::uppercase) }
                .ifNull { return CreateResult.Error },
            identifier = identifier,
            serialName = arguments
                .get<String>("serialName") { identifier }
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
