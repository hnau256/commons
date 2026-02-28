package org.hnau.commons.gen.loggable.processor

import org.hnau.commons.gen.loggable.annotations.Loggable
import kotlin.reflect.KClass

internal object AnnotationInfo {

    private val annotationClass: KClass<Loggable> = Loggable::class

    val nameWithPackage: String
        get() = annotationClass.qualifiedName!!

    val simpleName: String
        get() = annotationClass.simpleName!!
}
