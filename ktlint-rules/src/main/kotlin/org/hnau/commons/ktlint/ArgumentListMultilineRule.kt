package org.hnau.commons.ktlint

import com.pinterest.ktlint.rule.engine.core.api.ElementType.BINARY_EXPRESSION
import com.pinterest.ktlint.rule.engine.core.api.ElementType.DOT_QUALIFIED_EXPRESSION
import com.pinterest.ktlint.rule.engine.core.api.ElementType.EQ
import com.pinterest.ktlint.rule.engine.core.api.ElementType.FUNCTION_LITERAL
import com.pinterest.ktlint.rule.engine.core.api.ElementType.LPAR
import com.pinterest.ktlint.rule.engine.core.api.ElementType.OPERATION_REFERENCE
import com.pinterest.ktlint.rule.engine.core.api.ElementType.RPAR
import com.pinterest.ktlint.rule.engine.core.api.ElementType.TYPE_ARGUMENT_LIST
import com.pinterest.ktlint.rule.engine.core.api.ElementType.VALUE_ARGUMENT
import com.pinterest.ktlint.rule.engine.core.api.ElementType.VALUE_ARGUMENT_LIST
import com.pinterest.ktlint.rule.engine.core.api.IndentConfig
import com.pinterest.ktlint.rule.engine.core.api.Rule
import com.pinterest.ktlint.rule.engine.core.api.RuleId
import com.pinterest.ktlint.rule.engine.core.api.TokenSets
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.EditorConfig
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.INDENT_SIZE_PROPERTY
import com.pinterest.ktlint.rule.engine.core.api.editorconfig.INDENT_STYLE_PROPERTY
import com.pinterest.ktlint.rule.engine.core.api.indentWithoutNewlinePrefix
import com.pinterest.ktlint.rule.engine.core.api.isPartOfComment20
import com.pinterest.ktlint.rule.engine.core.api.isWhiteSpace20
import com.pinterest.ktlint.rule.engine.core.api.isWhiteSpaceWithNewline20
import com.pinterest.ktlint.rule.engine.core.api.parent
import com.pinterest.ktlint.rule.engine.core.api.prevLeaf
import com.pinterest.ktlint.rule.engine.core.api.replaceTextWith
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.psiUtil.children

/**
 * Требует, чтобы вызов функции с двумя и более аргументами всегда был многострочным:
 * каждый аргумент на своей строке, `(` и `)` — на отдельных строках.
 */
public class ArgumentListMultilineRule :
    Rule(
        ruleId = RuleId("hnau:argument-list-multiline"),
        about =
            Rule.About(
                maintainer = "hnau",
                repositoryUrl = "https://github.com/hnau256/commons",
                issueTrackerUrl = "https://github.com/hnau256/commons/issues",
            ),
        usesEditorConfigProperties = setOf(INDENT_SIZE_PROPERTY, INDENT_STYLE_PROPERTY),
    ) {

    private var editorConfigIndent = IndentConfig.DEFAULT_INDENT_CONFIG

    override fun beforeFirstNode(editorConfig: EditorConfig) {
        editorConfigIndent =
            IndentConfig(
                indentStyle = editorConfig[INDENT_STYLE_PROPERTY],
                tabWidth = editorConfig[INDENT_SIZE_PROPERTY],
            )
    }

    override fun beforeVisitChildNodes(
        node: ASTNode,
        autoCorrect: Boolean,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> Unit,
    ) {
        if (editorConfigIndent.disabled) {
            return
        }
        if (node.elementType != VALUE_ARGUMENT_LIST) {
            return
        }
        if (needToWrapArgumentList(node)) {
            node
                .children()
                .forEach { child ->
                    wrapArgumentInList(
                        child = child,
                        autoCorrect = autoCorrect,
                        emit = emit,
                    )
                }
        }
    }

    private fun needToWrapArgumentList(node: ASTNode): Boolean =
        node.children().count { it.elementType == VALUE_ARGUMENT } >= 2 &&
            node.parent?.elementType != FUNCTION_LITERAL &&
            !node.isAnnotationArgumentList()

    private fun ASTNode.isAnnotationArgumentList(): Boolean {
        var current = psi.parent
        repeat(3) {
            if (current is KtAnnotationEntry) {
                return true
            }
            current = current?.parent
        }
        return false
    }

    private fun intendedIndent(child: ASTNode): String =
        when {
            child.parent!!.hasTypeArgumentListInFront() -> -1
            child.parent!!.isPartOfDotQualifiedAssignmentExpression() -> -1
            else -> 0
        }.let {
            if (child.parent!!.isOnSameLineAsControlFlowKeyword()) {
                it + 1
            } else {
                it
            }
        }.let {
            if (child.elementType == VALUE_ARGUMENT) {
                it + 1
            } else {
                it
            }
        }.let { indentLevelFix ->
            val indentLevel =
                editorConfigIndent
                    .indentLevelFrom(child.parent!!.indentWithoutNewlinePrefix)
                    .plus(indentLevelFix)
            "\n" + editorConfigIndent.indent.repeat(maxOf(indentLevel, 0))
        }

    private fun wrapArgumentInList(
        child: ASTNode,
        autoCorrect: Boolean,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> Unit,
    ) {
        when (child.elementType) {
            LPAR -> {
                val prevLeaf = child.prevLeaf
                if (prevLeaf != null && prevLeaf.isWhiteSpaceWithNewline20) {
                    emit(child.startOffset, errorMessage(child), true)
                    if (autoCorrect) {
                        prevLeaf.psi.delete()
                    }
                }
            }

            VALUE_ARGUMENT,
            RPAR,
            -> {
                val intendedIndent = intendedIndent(child)
                val prevLeaf = child.prevWhiteSpaceWithNewLine() ?: child.prevLeaf
                when {
                    prevLeaf != null && prevLeaf.isWhiteSpaceWithNewline20 -> {
                        // Уже на новой строке — отступ выправит indentation rule (если включён).
                        return
                    }

                    prevLeaf != null && prevLeaf.isWhiteSpace20 -> {
                        emit(child.startOffset, errorMessage(child), true)
                        if (autoCorrect) {
                            prevLeaf.replaceTextWith(intendedIndent)
                        }
                    }

                    else -> {
                        emit(child.startOffset, errorMessage(child), true)
                        if (autoCorrect) {
                            child.parent?.addChild(
                                PsiWhiteSpaceImpl(intendedIndent),
                                child,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun errorMessage(node: ASTNode): String =
        when (node.elementType) {
            LPAR -> """Unnecessary newline before "(""""
            VALUE_ARGUMENT -> "Argument should be on a separate line"
            RPAR -> """Missing newline before ")""""
            else -> throw UnsupportedOperationException()
        }

    private fun ASTNode.hasTypeArgumentListInFront(): Boolean =
        parent
            ?.findChildByType(TYPE_ARGUMENT_LIST)
            ?.children()
            ?.any { it.isWhiteSpaceWithNewline20 } == true

    private fun ASTNode.isPartOfDotQualifiedAssignmentExpression(): Boolean =
        parent
            ?.parent
            ?.takeIf { it.elementType == BINARY_EXPRESSION }
            ?.let { binaryExpression ->
                binaryExpression.firstChildNode.elementType == DOT_QUALIFIED_EXPRESSION &&
                    binaryExpression.findChildByType(OPERATION_REFERENCE)?.firstChildNode?.elementType == EQ
            }
            ?: false

    private fun ASTNode.prevWhiteSpaceWithNewLine(): ASTNode? {
        var prev = prevLeaf
        while (prev != null && (prev.isWhiteSpace20 || prev.isPartOfComment20)) {
            if (prev.isWhiteSpaceWithNewline20) {
                return prev
            }
            prev = prev.prevLeaf
        }
        return null
    }

    private fun ASTNode.isOnSameLineAsControlFlowKeyword(): Boolean {
        var prevLeaf = prevLeaf ?: return false
        while (prevLeaf.elementType !in TokenSets.CONTROL_FLOW_KEYWORDS) {
            if (prevLeaf.isWhiteSpaceWithNewline20) {
                return false
            }
            prevLeaf = prevLeaf.prevLeaf ?: return false
        }
        return true
    }
}
