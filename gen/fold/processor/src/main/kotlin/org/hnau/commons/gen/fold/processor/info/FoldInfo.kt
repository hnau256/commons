package org.hnau.commons.gen.fold.processor.info

import arrow.core.NonEmptyList
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
data class FoldInfo(
    val classDeclaration: KSClassDeclaration,
    val variants: NonEmptyList<Variant>,
) {
    data class Variant(
        val identifier: String,
        val resolution: Resolution,
        val className: ClassName,
    )

    sealed interface Resolution {
        data class Destructured(
            val parameters: List<Parameter>,
        ) : Resolution

        data class Whole(
            val type: ClassName,
        ) : Resolution

        data object Object : Resolution
    }

    data class Parameter(
        val name: String,
        val type: ClassName,
    )

    companion object
}
