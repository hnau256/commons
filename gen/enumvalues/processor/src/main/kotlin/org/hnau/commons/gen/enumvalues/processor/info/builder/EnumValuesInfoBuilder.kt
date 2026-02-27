package org.hnau.commons.gen.enumvalues.processor.info.builder

import arrow.core.toNonEmptyListOrNull
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import org.hnau.commons.gen.enumvalues.annotations.EnumValues
import org.hnau.commons.gen.enumvalues.processor.AnnotationInfo
import org.hnau.commons.gen.enumvalues.processor.info.EnumValuesInfo
import org.hnau.commons.gen.kotlin.arguments
import org.hnau.commons.gen.kotlin.stickedName
import org.hnau.commons.kotlin.ifNull

fun EnumValuesInfo.Companion.create(
    logger: KSPLogger,
    annotated: KSAnnotated,
): EnumValuesInfo? {

    if (!annotated.validate()) {
        logger.error("Is not valid", annotated)
        return null
    }

    val classDeclaration = (annotated as? KSClassDeclaration).ifNull {
        logger.error("Is not class", annotated)
        return null
    }

    if (classDeclaration.classKind != ClassKind.ENUM_CLASS) {
        logger.error("Is not enum class", classDeclaration)
        return null
    }

    val enumValuesAnnotation = classDeclaration
        .annotations
        .firstOrNull { it.shortName.asString() == AnnotationInfo.simpleName }
        .ifNull {
            logger.error("Unable find @${AnnotationInfo.simpleName} annotation", classDeclaration)
            return null
        }

    val arguments = enumValuesAnnotation.arguments(logger)

    val stickedName = classDeclaration
        .stickedName(logger)
        .ifNull { return null }

    return EnumValuesInfo(
        enumClass = classDeclaration,
        serializable = arguments
            .get<Boolean>(
                name = "serializable",
                onNoArgument = { EnumValues.defaultSerializable },
            )
            ?: return null,
        valuesClass = arguments
            .get<String>(
                name = "valuesClassName",
                onNoArgument = { "" },
            )
            .ifNull { return null }
            .takeIf(String::isNotEmpty)
            .ifNull { stickedName + "Values" },
        enumIdentifier = stickedName.replaceFirstChar(Char::lowercase),
        entries = classDeclaration
            .declarations
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.classKind == ClassKind.ENUM_ENTRY }
            .map { entry ->
                entry
                    .simpleName
                    .asString()
                    .let(EnumValuesInfo::Entry)
            }
            .toList()
            .toNonEmptyListOrNull()
            .ifNull {
                logger.error("Enum has no entries", classDeclaration)
                return null
            },
    )
}