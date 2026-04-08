package org.hnau.commons.app.projector.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.flow.MutableStateFlow
import org.hnau.commons.app.model.EditingString
import org.hnau.commons.kotlin.MutableAccessor
import org.hnau.commons.kotlin.mapper.Mapper


private val editingStringTextFieldValueMapper = Mapper<EditingString, TextFieldValue>(
    direct = { editingString ->
        TextFieldValue(
            text = editingString.text,
            selection = TextRange(
                start = editingString.selection.first,
                end = editingString.selection.last,
            ),
        )
    },
    reverse = { textFieldValue ->
        EditingString(
            text = textFieldValue.text,
            selection = IntRange(
                start = textFieldValue.selection.min,
                endInclusive = textFieldValue.selection.max,
            ),
        )
    },
)

val EditingString.Companion.textFieldValueMapper: Mapper<EditingString, TextFieldValue>
    get() = editingStringTextFieldValueMapper

@Composable
fun MutableStateFlow<EditingString>.collectAsTextFieldValueMutableAccessor(): MutableAccessor<TextFieldValue> =
    collectAsMutableAccessor(EditingString.textFieldValueMapper)
