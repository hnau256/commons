package org.hnau.commons.gen.enumvalues.processor.info

import arrow.core.NonEmptyList
import com.google.devtools.ksp.symbol.KSClassDeclaration

data class EnumValuesInfo(
    val enumClass: KSClassDeclaration,
    val serializable: Boolean,
    val valuesClass: String,
    val enumIdentifier: String,
    val entries: NonEmptyList<Entry>,
) {

    @JvmInline
    value class Entry(
        val name: String,
    )

    companion object
}