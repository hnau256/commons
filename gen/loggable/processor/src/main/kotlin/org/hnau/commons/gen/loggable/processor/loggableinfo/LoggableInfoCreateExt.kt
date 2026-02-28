package org.hnau.commons.gen.loggable.processor.loggableinfo

import com.google.devtools.ksp.symbol.KSClassDeclaration
import org.hnau.commons.gen.loggable.processor.AnnotationInfo
import org.hnau.commons.kotlin.castOrNull
import org.hnau.commons.kotlin.ifNull

internal fun LoggableInfo.Companion.create(
    classDeclaration: KSClassDeclaration,
): LoggableInfo {

    val className = classDeclaration.simpleName.asString()

    return LoggableInfo(

        className = className,

        classPackage = classDeclaration
            .packageName
            .asString(),

        tag = classDeclaration
            .annotations
            .first { it.shortName.asString() == AnnotationInfo.simpleName }
            .arguments
            .find { it.name?.asString() == "tag" }
            ?.value.castOrNull<String>()
            ?.takeIf(String::isNotEmpty)
            .ifNull { className },

        hasCompanion = classDeclaration
            .declarations
            .filterIsInstance<KSClassDeclaration>()
            .any(KSClassDeclaration::isCompanionObject),
    )
}