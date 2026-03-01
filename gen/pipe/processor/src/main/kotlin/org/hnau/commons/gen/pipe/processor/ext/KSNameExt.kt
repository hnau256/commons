package org.hnau.commons.gen.pipe.processor.ext

import com.google.devtools.ksp.symbol.KSName

internal fun KSName(
    packageName: String,
    simpleName: String,
): KSName = object : KSName {

    override fun getQualifier(): String = "$packageName.$simpleName"

    override fun asString(): String = getQualifier()

    override fun getShortName(): String = simpleName
}

internal val KSName.log: String
    get() = asString()

internal val KSName.packageName: String
    get() {
        val full = getQualifier()
        val short = getShortName()
        return full.removeSuffix(short).dropLast(1)
    }