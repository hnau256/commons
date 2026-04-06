package org.hnau.commons.gen.sealup.processor.sealedinfo

import arrow.core.NonEmptyList
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.Visibility
import com.squareup.kotlinpoet.ClassName

sealed interface CreateResult<out T> {
    data class Success<T>(
        val value: T,
    ) : CreateResult<T>

    data object Deferred : CreateResult<Nothing>

    data object Error : CreateResult<Nothing>
}

data class SealedInfo(
    val parent: KSClassDeclaration,
    val variants: NonEmptyList<Variant>,
    val serializable: Boolean,
    val ordinal: Boolean,
    val name: Boolean,
    val sealedInterfaceName: String,
    val fold: Boolean,
    val factoryMethods: ParentExtension?,
    val overrides: List<Override>,
) {
    data class ParentExtension(
        val companion: KSClassDeclaration,
    )

    data class Variant(
        val wrappedType: KSType,
        val wrapperClass: String,
        val wrappedClassName: ClassName,
        val identifier: String,
        val serialName: String,
        val wrappedIdentifier: String,
        val constructors: List<Constructor>,
        val isObject: Boolean,
    ) {
        data class Constructor(
            val parameters: List<Parameter>,
        ) {
            data class Parameter(
                val name: String?,
                val type: KSType,
            )
        }

        companion object
    }

    data class Override(
        val name: String,
        val result: KSType,
        val type: Type,
        val visibility: Visibility,
        val receiver: KSType?,
        val typeParameters: List<KSTypeParameter>,
        val annotations: List<KSAnnotation>,
    ) {
        sealed interface Type {
            data class Function(
                val arguments: List<Argument>,
            ) : Type {
                data class Argument(
                    val name: String,
                    val type: KSType,
                )
            }

            data class Property(
                val mutable: Boolean,
            ) : Type
        }

        companion object
    }

    companion object
}
