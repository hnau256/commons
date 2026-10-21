package org.hnau.commons.gen.pipe.processor.impl

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSName
import org.hnau.commons.gen.pipe.processor.data.Argument
import org.hnau.commons.gen.pipe.processor.ext.argumants
import org.hnau.commons.gen.pipe.processor.ext.implementationName
import org.hnau.commons.gen.pipe.processor.ext.log
import org.hnau.commons.gen.pipe.processor.ext.packageName
import org.hnau.commons.gen.pipe.processor.ext.qualifiedNameOrThrow
import org.hnau.commons.kotlin.ifNull

internal data class ForeignDependentImplementation(
    override val impl: KSName,
    override val arguments: List<Argument>,
) : Dependent {

    companion object {

        @OptIn(KspExperimental::class)
        fun create(
            resolver: Resolver,
            logger: KSPLogger,
            interfaceDeclaration: KSClassDeclaration,
        ): ForeignDependentImplementation {
            val interfaceLog = interfaceDeclaration.qualifiedNameOrThrow.log
            val implementationName = interfaceDeclaration.implementationName
            logger.info("Getting implementation: ${implementationName.log} for pipe segment $interfaceLog")

            val implementation = resolver
                .getClassDeclarationByName(implementationName)
                .ifNull {
                    resolver
                        .getDeclarationsFromPackage(implementationName.packageName)
                        .filterIsInstance<KSClassDeclaration>()
                        .firstOrNull { it.qualifiedNameOrThrow.asString() == implementationName.asString() }
                }
                ?: error("Unable find implementation (${implementationName.log}) of ${interfaceDeclaration.log}")

            val constructor = implementation
                .primaryConstructor
                ?: error("${implementation.log} has no primary constructor")
            return ForeignDependentImplementation(
                impl = implementationName,
                arguments = constructor.argumants,
            )
        }
    }
}