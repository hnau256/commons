package org.hnau.commons.gen.sealup.processor.sealedinfo.generator.utils

import com.google.devtools.ksp.getVisibility
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toKModifier
import org.hnau.commons.gen.kotlin.CodeBlockBuilderContext
import org.hnau.commons.gen.sealup.processor.sealedinfo.SealedInfo
import org.hnau.commons.kotlin.it

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

val SealedInfo.Variant.uppercasedIdentifier: String
    get() = identifier.replaceFirstChar(Char::uppercase)

val SealedInfo.Variant.Wrapped.className: ClassName
    get() = type.toClassName()

fun CodeBlockBuilderContext.use(
    wrapped: SealedInfo.Variant.Wrapped,
): String = wrapped.pointer.fold(
    ifClass = ::it,
    ifObject = { use(wrapped.className) }
)

inline fun <R> SealedInfo.Variant.Wrapped.Pointer.fold(
    ifClass: (String) -> R,
    ifObject: () -> R,
): R = when (this) {
    is SealedInfo.Variant.Wrapped.Pointer.Class -> ifClass(property)
    SealedInfo.Variant.Wrapped.Pointer.Object -> ifObject()
}
