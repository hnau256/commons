package org.hnau.commons.gen.fold.processor.info

import arrow.core.NonEmptyList
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
data class FoldInfo(
    val classDeclaration: KSClassDeclaration,
    val typeVariables: List<TypeVariableName>,
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
            val type: TypeName,
        ) : Resolution

        data object Object : Resolution
    }

    data class Parameter(
        val name: String,
        val type: TypeName,
    )

    companion object
}
