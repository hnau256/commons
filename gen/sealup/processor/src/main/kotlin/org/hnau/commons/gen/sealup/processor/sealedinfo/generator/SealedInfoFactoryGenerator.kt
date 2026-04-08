package org.hnau.commons.gen.sealup.processor.sealedinfo.generator

import arrow.core.toNonEmptyListOrNull
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ksp.toTypeName
import org.hnau.commons.gen.sealup.processor.sealedinfo.SealedInfo
import org.hnau.commons.gen.sealup.processor.sealedinfo.generator.utils.className
import org.hnau.commons.gen.sealup.processor.sealedinfo.generator.utils.companionClassName
import org.hnau.commons.gen.sealup.processor.sealedinfo.generator.utils.visibility
import org.hnau.commons.gen.sealup.processor.sealedinfo.generator.utils.wrapperClassName
import org.hnau.commons.kotlin.ifFalse
import org.hnau.commons.kotlin.ifTrue

fun SealedInfo.toFactoriesFuncsSpec(
    parentExtension: SealedInfo.ParentExtension,
): List<FunSpec> = variants
    .toList()
    .flatMap { variant ->
        variant.toFactoriesFuncsSpec(
            info = this,
            parentExtension = parentExtension,
        )
    }

private fun SealedInfo.Variant.toFactoriesFuncsSpec(
    info: SealedInfo,
    parentExtension: SealedInfo.ParentExtension,
): List<FunSpec> {
    val wrapperClassName = wrapperClassName(info)
    return buildList {
        add(
            toFactoryFuncSpec(
                info = info,
                parentExtension = parentExtension,
                wrapperClassName = wrapperClassName,
            ),
        )
        addAll(
            constructors
                .map { constructor ->
                    toConstructorFactoryFuncSpec(
                        info = info,
                        parentExtension = parentExtension,
                        wrapperClassName = wrapperClassName,
                        constructor = constructor,
                    )
                },
        )
    }
}

private fun SealedInfo.Variant.toFactoryFuncSpec(
    info: SealedInfo,
    parentExtension: SealedInfo.ParentExtension,
    wrapperClassName: ClassName,
): FunSpec = FunSpec
    .builder(identifier)
    .apply {
        info.visibility?.let { modifiers += it }

        receiver(parentExtension.companionClassName)
        returns(wrapperClassName)

        isObject.ifFalse {
            addParameter(
                name = identifier,
                type = wrapped.className,
            )
        }
    }
    .addCode(
        isObject
            .ifFalse { "($wrapped.identifier = $identifier)" }
            .orEmpty()
            .let { constructorCall ->
                "return %T$constructorCall"
            },
        wrapperClassName,
    )
    .build()

private fun SealedInfo.Variant.toConstructorFactoryFuncSpec(
    info: SealedInfo,
    parentExtension: SealedInfo.ParentExtension,
    wrapperClassName: ClassName,
    constructor: SealedInfo.Variant.Constructor,
): FunSpec {

    val allParametersHasNames = constructor
        .parameters
        .all { (name) -> name != null }

    return FunSpec
        .builder(identifier)
        .apply {
            info.visibility?.let { modifiers += it }

            receiver(parentExtension.companionClassName)
            returns(wrapperClassName)

            constructor
                .parameters
                .forEachIndexed { index, (nameOrNull, type) ->
                    val name = nameOrNull ?: "parameter$index"
                    addParameter(
                        name,
                        type.toTypeName(),
                    )
                }
        }
        .addCode(
            Pair(
                "return $identifier(\n\t$identifier = %T(",
                ")\n)",
            ).let { (prefix, postfix) ->
                constructor
                    .parameters
                    .toNonEmptyListOrNull()
                    ?.withIndex()
                    ?.joinToString(
                        prefix = "$prefix\n",
                        postfix = "\n\t$postfix",
                        separator = "\n",
                    ) { (index, nameWithType) ->
                        val (nameOrNull) = nameWithType
                        val name = nameOrNull ?: "parameter$index"
                        val prefix = allParametersHasNames.ifTrue { "$name = " }.orEmpty()
                        "\t\t$prefix$name,"
                    }
                    ?: "$prefix$postfix"
            },
            wrapped.className,
        )
        .build()
}
