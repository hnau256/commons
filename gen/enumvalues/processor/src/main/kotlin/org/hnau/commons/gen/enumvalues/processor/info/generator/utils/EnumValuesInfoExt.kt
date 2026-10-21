package org.hnau.commons.gen.enumvalues.processor.info.generator.utils

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName
import org.hnau.commons.gen.enumvalues.processor.info.EnumValuesInfo

val EnumValuesInfo.packageName: String
    get() = enumClass.packageName.asString()

val EnumValuesInfo.enumClassName: ClassName
    get() = enumClass.toClassName()

val EnumValuesInfo.valuesClassName: ClassName
    get() = ClassName(packageName, valuesClass)

val EnumValuesInfo.Entry.identifier: String
    get() = name.replaceFirstChar(Char::lowercase)