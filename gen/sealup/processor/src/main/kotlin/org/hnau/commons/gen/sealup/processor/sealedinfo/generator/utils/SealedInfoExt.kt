package org.hnau.commons.gen.sealup.processor.sealedinfo.generator.utils

import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.processing.KSPLogger
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toKModifier
import org.hnau.commons.gen.sealup.processor.sealedinfo.SealedInfo

val SealedInfo.packageName: String
    get() = parent.packageName.asString()

val SealedInfo.className: ClassName
    get() = ClassName(packageName, sealedInterfaceName)

val SealedInfo.visibility: KModifier?
    get() = parent.getVisibility().toKModifier()

val SealedInfo.parentClassName: ClassName
    get() = parent.toClassName()

val SealedInfo.ParentExtension.companionClassName: ClassName
    get() = companion.toClassName()

fun SealedInfo.Variant.wrapperClassName(
    sealedInfo: SealedInfo,
): ClassName = sealedInfo
    .className
    .let { sealedClassName ->
        ClassName(
            packageName = sealedClassName.packageName,
            simpleNames = sealedClassName.simpleNames + wrapperClass,
        )
    }

fun SealedInfo.Variant.wrappedIdentifierOrError(
    logger: KSPLogger,
) = when (wrapped) {
    SealedInfo.Variant.Wrapped.None -> {
        logger.error("Variant '$identifier' has no wrapped class")
        null
    }

    is SealedInfo.Variant.Wrapped.Some -> when (val mode = wrapped.mode) {
        is SealedInfo.Variant.Wrapped.Some.Mode.Class -> mode.identifier
        is SealedInfo.Variant.Wrapped.Some.Mode.Object -> mode.identifier
    }
}

val SealedInfo.Variant.uppercasedIdentifier: String
    get() = identifier.replaceFirstChar(Char::uppercase)
