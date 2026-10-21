package org.hnau.commons.gen.kotlin

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotation
import org.hnau.commons.kotlin.foldNullable
import org.hnau.commons.kotlin.ifNull

class AnnotationArguments(
    @PublishedApi
    internal val annotation: KSAnnotation,
    @PublishedApi
    internal val logger: KSPLogger,
) {

    @PublishedApi
    internal val arguments: Map<String, Any> = annotation
        .arguments
        .associate { argument ->
            val name = argument.name!!.asString()
            val value = argument.value!!
            name to value
        }

    @PublishedApi
    internal val argumentsNames: String by lazy {
        arguments
            .toList()
            .joinToString(
                prefix = "[",
                postfix = "]",
            ) { (name) -> "'$name'" }
    }

    inline fun <reified T : Any> get(
        name: String,
        onNoArgument: () -> T? = {
            logger.error(
                "There is no argument with name '$name'. Available arguments: $argumentsNames",
                annotation,
            )
            null
        },
    ): T? = arguments[name].foldNullable(
        ifNull = onNoArgument,
        ifNotNull = { value ->
            (value as? T).ifNull {
                logger.error(
                    "Expected type of argument '$name' is ${T::class}, actual is ${value.javaClass}",
                    annotation
                )
                null
            }
        }
    )
}

fun KSAnnotation.arguments(
    logger: KSPLogger,
): AnnotationArguments = AnnotationArguments(
    annotation = this,
    logger = logger,
)