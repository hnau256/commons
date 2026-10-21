package org.hnau.commons.gen.pipe.processor.ext

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSName

internal val KSClassDeclaration.hasCompanionObject: Boolean
    get() = declarations.any { it is KSClassDeclaration && it.isCompanionObject }

internal val KSClassDeclaration.implementationName: KSName
    get() {
        val packageName = packageName.asString()
        return KSName(
            packageName = packageName,
            simpleName = qualifiedNameOrThrow
                .asString()
                .removePrefix(packageName)
                .drop(1)
                .replace(".", "")
                .plus("Impl"),
        )
    }