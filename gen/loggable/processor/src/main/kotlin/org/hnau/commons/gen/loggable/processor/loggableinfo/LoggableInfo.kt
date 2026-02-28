package org.hnau.commons.gen.loggable.processor.loggableinfo

internal data class LoggableInfo(
    val className: String,
    val classPackage: String,
    val tag: String,
    val hasCompanion: Boolean,
) {

    companion object
}
