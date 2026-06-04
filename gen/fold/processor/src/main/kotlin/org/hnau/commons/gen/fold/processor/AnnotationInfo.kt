package org.hnau.commons.gen.fold.processor

import org.hnau.commons.gen.fold.annotations.Fold
import kotlin.reflect.KClass

internal object AnnotationInfo {

    private val annotationClass: KClass<Fold> = Fold::class

    val nameWithPackage: String
        get() = annotationClass.qualifiedName!!

    val simpleName: String
        get() = annotationClass.simpleName!!
}
