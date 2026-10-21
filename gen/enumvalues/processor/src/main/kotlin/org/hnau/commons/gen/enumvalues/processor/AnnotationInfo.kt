package org.hnau.commons.gen.enumvalues.processor

import org.hnau.commons.gen.enumvalues.annotations.EnumValues
import kotlin.reflect.KClass

internal object AnnotationInfo {

    private val annotationClass: KClass<EnumValues> = EnumValues::class

    val nameWithPackage: String
        get() = annotationClass.qualifiedName!!

    val simpleName: String
        get() = annotationClass.simpleName!!
}