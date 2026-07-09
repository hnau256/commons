package org.hnau.commons.gen.loggable.processor.loggableinfo

import com.google.devtools.ksp.symbol.KSClassDeclaration

internal data class LoggableInfo(
    val loggableClassDeclaration: KSClassDeclaration,
    val classPackage: String,
    val classSimpleNames: List<String>,
    val tag: String,
    val hasCompanion: Boolean,
) {

    companion object
}
