package org.hnau.commons.gen.kotlin

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock

interface CodeBlockBuilderContext {

    fun use(
        className: ClassName,
    ): String

}

@PublishedApi
internal class CodeBlockBuilderContextImpl : CodeBlockBuilderContext {

    private val _arguments: MutableList<Any?> = mutableListOf()

    val arguments: Array<Any?>
        get() = _arguments.toTypedArray()

    private fun use(
        placeholder: String,
        argument: Any?,
    ): String {
        _arguments += argument
        return "%$placeholder"
    }

    override fun use(
        className: ClassName,
    ): String = use(
        placeholder = "T",
        argument = className,
    )
}

inline fun codeBlock(
    build: CodeBlockBuilderContext.() -> String,
): CodeBlock = CodeBlockBuilderContextImpl().run {
    CodeBlock.of(
        format = build(),
        args = arguments,
    )
}