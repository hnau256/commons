package org.hnau.commons.gen.sealup.processor

import org.hnau.commons.gen.sealup.annotations.SealUp
import kotlin.reflect.KClass

internal object AnnotationInfo {

    private val annotationClass: KClass<SealUp> = SealUp::class

    val nameWithPackage: String
        get() = annotationClass.qualifiedName!!

    val simpleName: String
        get() = annotationClass.simpleName!!
}