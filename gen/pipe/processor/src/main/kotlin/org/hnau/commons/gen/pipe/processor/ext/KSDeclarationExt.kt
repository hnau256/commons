package org.hnau.commons.gen.pipe.processor.ext

import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSName

val KSDeclaration.log: String
    get() = qualifiedName?.asString() ?: "<unknown>"

val KSDeclaration.shortName: String
    get() = qualifiedNameOrThrow.asString().removePrefix(packageName.asString()).drop(1)

val KSDeclaration.qualifiedNameOrThrow: KSName
    get() = qualifiedName ?: error("Unable resolve qualified name of $log")