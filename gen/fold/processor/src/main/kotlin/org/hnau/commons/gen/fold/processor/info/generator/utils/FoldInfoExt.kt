package org.hnau.commons.gen.fold.processor.info.generator.utils

import com.google.devtools.ksp.symbol.ClassKind
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName
import org.hnau.commons.gen.fold.processor.info.FoldInfo

val FoldInfo.packageName: String
    get() = classDeclaration.packageName.asString()

val FoldInfo.className: ClassName
    get() = classDeclaration.toClassName()

val FoldInfo.fileName: String
    get() = classDeclaration.simpleName.asString() + "Fold"

val FoldInfo.isEnum: Boolean
    get() = classDeclaration.classKind == ClassKind.ENUM_CLASS

val FoldInfo.Variant.uppercasedIdentifier: String
    get() = identifier.replaceFirstChar(Char::uppercase)
