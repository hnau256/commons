package hnau.commons.gen.kotlin

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSName
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import hnau.commons.kotlin.foldNullable
import hnau.commons.kotlin.ifNull

@Deprecated("")
fun KSAnnotation.toAnnotationSpec(
    logger: KSPLogger,
): AnnotationSpec? = annotationType
    .resolve(logger)
    ?.declaration
    ?.let { declaration ->
        ClassName(
            packageName = declaration.packageName.asString(),
            declaration.simpleName.asString(),
        )
    }
    ?.let { className ->
        AnnotationSpec
            .builder(className)
            .apply {
                arguments.forEach { arg ->
                    val name = arg.name?.asString()
                    val rawValue = arg.value.ifNull {
                        logger.error("Unable extract value of argument '$name'", this@toAnnotationSpec)
                        return null
                    }

                    val code = valueToCode(rawValue)

                    name.foldNullable(
                        ifNull = { addMember("%L", code) },
                        ifNotNull = { name -> addMember("%L = %L", name, code) }
                    )
                }
            }
            .build()
    }


private fun valueToCode(
    value: Any,
): CodeBlock = when (value) {

    is String ->
        CodeBlock.of("%S", value)

    is Boolean, is Int, is Long, is Float, is Double ->
        CodeBlock.of("%L", value)

    is KSName ->
        CodeBlock.of("%L", value.asString())

    is List<*> -> CodeBlock.builder()
        .add("[")
        .apply {
            value
                .filterNotNull()
                .map(::valueToCode)
                .forEachIndexed { index, block ->
                    if (index > 0) {
                        add(", ")
                    }
                    add(block)
                }
        }
        .add("]")
        .build()


    else ->
        CodeBlock.of("%L", value)
}
