package org.hnau.commons.gen.pipe.processor.impl

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSType
import org.hnau.commons.gen.pipe.processor.data.Argument

internal data class InterfaceToImplement(
    val file: KSFile,
    val declaration: KSClassDeclaration,
    val properties: List<Argument>,
    val factoryMethods: List<FactoryMethod>,
    val hasCompanionObject: Boolean,
) {

    data class FactoryMethod(
        val name: String,
        val result: KSType,
        val parameters: List<Argument>,
    )

    companion object
}



