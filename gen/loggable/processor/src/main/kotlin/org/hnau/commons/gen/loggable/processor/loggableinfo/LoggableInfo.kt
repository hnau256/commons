package org.hnau.commons.gen.loggable.processor.loggableinfo

import com.google.devtools.ksp.symbol.KSClassDeclaration

internal data class LoggableInfo(
    val loggableClassDeclaration: KSClassDeclaration,
    val className: String,
    val classPackage: String,
    val tag: String,
    val hasCompanion: Boolean,
) {

    companion object
}
