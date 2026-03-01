package org.hnau.commons.gen.pipe.processor.impl

import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSType
import org.hnau.commons.gen.pipe.processor.data.Argument
import org.hnau.commons.gen.pipe.processor.ext.implementationName

internal data class ImplementedInterface(
    val interfaceToImplement: InterfaceToImplement,
    val overrideArguments: List<Argument>,
    val privateArguments: List<Argument>,
    val factoryMethods: List<FactoryMethod>,
) : Dependent {

    override val arguments: List<Argument> =
        overrideArguments + privateArguments

    override val impl: KSName =
        interfaceToImplement.declaration.implementationName

    data class FactoryMethod(
        val name: String,
        val result: KSType,
        val resultImpl: KSName,
        val arguments: List<Argument>,
        val dependentConstructorArguments: List<Pair<String, String>>,
    )
}