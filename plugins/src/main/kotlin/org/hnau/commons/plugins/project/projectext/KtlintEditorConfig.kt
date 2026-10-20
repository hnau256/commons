package org.hnau.commons.plugins.project.projectext

/**
 * Набор правил ktlint, который convention-плагины применяют автоматически.
 *
 * Задаётся через `additionalEditorconfig`, поэтому проекту-потребителю
 * не нужен собственный `.editorconfig` — достаточно подключить плагин.
 */
internal val ktlintEditorConfig: Map<String, String> = mapOf(
    "ktlint_code_style" to "intellij_idea",
    // Параметры всегда построчно, тело — по возможности на строке сигнатуры.
    "ktlint_function_signature_rule_force_multiline_when_parameter_count_greater_or_equal_than" to "1",
    "ktlint_class_signature_rule_force_multiline_when_parameter_count_greater_or_equal_than" to "1",
    "ktlint_function_signature_body_expression_wrapping" to "default",
    // Висячие запятые; порядок импортов как в IntelliJ.
    "ij_kotlin_allow_trailing_comma" to "true",
    "ij_kotlin_allow_trailing_comma_on_call_site" to "true",
    "ij_kotlin_imports_layout" to "*,java.**,javax.**,kotlin.**",
    // Не навязываем агрессивный reflow/indent и правила, расходящиеся со стилем.
    "ktlint_standard_indent" to "disabled",
    "ktlint_standard_multiline-expression-wrapping" to "disabled",
    "ktlint_standard_chain-method-continuation" to "disabled",
    "ktlint_standard_function-expression-body" to "disabled",
    "ktlint_standard_blank-line-between-when-conditions" to "disabled",
    "ktlint_standard_no-empty-first-line-in-method-block" to "disabled",
    "ktlint_standard_no-empty-first-line-in-class-body" to "disabled",
    "ktlint_standard_when-entry-bracing" to "disabled",
    "ktlint_standard_function-naming" to "disabled",
    "ktlint_standard_property-naming" to "disabled",
    "ktlint_standard_no-consecutive-blank-lines" to "disabled",
    "ktlint_standard_no-blank-line-before-rbrace" to "disabled",
    "ktlint_standard_binary-expression-wrapping" to "disabled",
    "ktlint_standard_argument-list-wrapping" to "disabled",
    "ktlint_standard_enum-wrapping" to "disabled",
    "ktlint_standard_property-wrapping" to "disabled",
    "ktlint_standard_multiline-if-else" to "disabled",
    "ktlint_standard_if-else-wrapping" to "disabled",
    "ktlint_standard_no-blank-line-in-list" to "disabled",
    "ktlint_standard_spacing-between-declarations-with-comments" to "disabled",
    "ktlint_standard_comment-wrapping" to "disabled",
    "ktlint_standard_max-line-length" to "disabled",
    "ktlint_standard_kdoc" to "disabled",
    "ktlint_standard_no-consecutive-comments" to "disabled",
    "ktlint_standard_backing-property-naming" to "disabled",
)
