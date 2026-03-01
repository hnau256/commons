package org.hnau.commons.gen.pipe.processor.utils

import org.hnau.commons.gen.pipe.annotations.Pipe
import kotlin.reflect.KClass

internal object PipeAnnotationClassInfo {

    private val annotationClass: KClass<Pipe> = Pipe::class

    val nameWithPackage: String
        get() = annotationClass.qualifiedName!!

    val simpleName: String
        get() = annotationClass.simpleName!!
}