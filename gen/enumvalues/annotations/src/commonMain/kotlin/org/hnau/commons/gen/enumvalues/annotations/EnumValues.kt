package org.hnau.commons.gen.enumvalues.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class EnumValues(
    val serializable: Boolean = defaultSerializable,
    val valuesClassName: String = "",
) {

    companion object {

        const val defaultSerializable: Boolean = false
    }
}