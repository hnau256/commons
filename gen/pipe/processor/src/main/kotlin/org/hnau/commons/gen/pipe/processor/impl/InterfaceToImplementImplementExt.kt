package org.hnau.commons.gen.pipe.processor.impl

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import org.hnau.commons.gen.pipe.processor.data.Argument
import org.hnau.commons.gen.pipe.processor.ext.log
import org.hnau.commons.gen.pipe.processor.ext.qualifiedNameOrThrow

internal fun Iterable<InterfaceToImplement>.implement(
    resolver: Resolver,
    logger: KSPLogger,
    codeGenerator: CodeGenerator,
) {
    val implemented = HashMap<KSClassDeclaration, ImplementedInterface>()
    var toImplement: Set<InterfaceToImplement> = toSet()
    val toImplementLog = toImplement.joinToString { it.declaration.qualifiedNameOrThrow.log }
    logger.info("Need implement: $toImplementLog")

    do {
        val implementedOnThisStep = toImplement
            .mapNotNull { interfaceToImplement ->
                val implementedInterface = interfaceToImplement
                    .tryImplement(
                        alreadyImplemented = implemented,
                        toImplement = toImplement,
                        resolver = resolver,
                        logger = logger,
                    )
                    ?: return@mapNotNull null
                implementedInterface.generateFile(
                    codeGenerator = codeGenerator,
                )
                implemented[interfaceToImplement.declaration] = implementedInterface
                interfaceToImplement
            }
            .toSet()
        toImplement = toImplement - implementedOnThisStep
        if (toImplement.isEmpty()) {
            return
        }

        if (implementedOnThisStep.isEmpty()) {
            val remainingInterfaces = toImplement.joinToString(
                separator = ", ",
                prefix = "[",
                postfix = "]",
                transform = { it.declaration.qualifiedNameOrThrow.log },
            )
            error("Unable resolve dependencies tree, maybe there are circled dependencies between $remainingInterfaces")
        }
    } while (true)
}

private fun InterfaceToImplement.tryImplement(
    resolver: Resolver,
    logger: KSPLogger,
    toImplement: Set<InterfaceToImplement>,
    alreadyImplemented: Map<KSClassDeclaration, ImplementedInterface>,
): ImplementedInterface? {


    val dependents: Map<InterfaceToImplement.FactoryMethod, Dependent> =
        factoryMethods.associateWith { dependent ->
            val dependentType = dependent.result
            val dependentDeclaration = dependentType.declaration as KSClassDeclaration
            alreadyImplemented[dependentDeclaration] ?: run {
                val willBeImplementedInFuture =
                    toImplement.any { it.declaration == dependentDeclaration }
                if (willBeImplementedInFuture) {
                    null
                } else {
                    ForeignDependentImplementation.create(
                        interfaceDeclaration = dependentDeclaration,
                        resolver = resolver,
                        logger = logger,
                    )
                }
            } ?: return null
        }

    logger.info("Implementing interface ${declaration.qualifiedNameOrThrow.log}")

    val overrideConstructorProperties = properties
    val privateConstructorProperties = mutableListOf<Argument>()

    val factoryMethods = dependents.map { (factoryMethod, dependentImplementation) ->
        factoryMethod.implement(
            interfaceClassInfo = declaration,
            dependentImplementation = dependentImplementation,
            overrideConstructorProperties = overrideConstructorProperties,
            privateConstructorProperties = privateConstructorProperties,
        )
    }

    return ImplementedInterface(
        interfaceToImplement = this,
        overrideArguments = overrideConstructorProperties,
        privateArguments = privateConstructorProperties,
        factoryMethods = factoryMethods,
    )
}

private fun InterfaceToImplement.FactoryMethod.implement(
    interfaceClassInfo: KSClassDeclaration,
    dependentImplementation: Dependent,
    overrideConstructorProperties: List<Argument>,
    privateConstructorProperties: MutableList<Argument>,
): ImplementedInterface.FactoryMethod {
    val factoryMethodsParameters = parameters.toMutableList()
    val result = ImplementedInterface.FactoryMethod(
        name = name,
        result = result,
        resultImpl = dependentImplementation.impl,
        arguments = parameters,
        dependentConstructorArguments = dependentImplementation
            .arguments
            .map { dependentConstructorArgument ->
                val key = dependentConstructorArgument.name
                val factoryMethodParameter =
                    factoryMethodsParameters.firstOrNull { parameterInfo ->
                        parameterInfo.type == dependentConstructorArgument.type
                    }
                if (factoryMethodParameter != null) {
                    factoryMethodsParameters.remove(factoryMethodParameter)
                    return@map key to factoryMethodParameter.name
                }
                val overrideConstructorProperty =
                    overrideConstructorProperties.firstOrNull { parameterInfo ->
                        parameterInfo.type == dependentConstructorArgument.type
                    }
                if (overrideConstructorProperty != null) {
                    return@map key to overrideConstructorProperty.name
                }
                val existencePrivateConstructorProperty =
                    privateConstructorProperties.firstOrNull { parameterInfo ->
                        parameterInfo.type == dependentConstructorArgument.type
                    }
                if (existencePrivateConstructorProperty != null) {
                    return@map key to existencePrivateConstructorProperty.name
                }
                privateConstructorProperties.add(
                    Argument(
                        name = key,
                        type = dependentConstructorArgument.type,
                    )
                )
                key to key
            }
    )
    if (factoryMethodsParameters.isNotEmpty()) {
        val unusedParameters = factoryMethodsParameters.joinToString(
            prefix = "[",
            postfix = "]",
            separator = ", ",
            transform = { propertyInfo -> propertyInfo.name },
        )
        error("Factory method `$name` of interface `${interfaceClassInfo.log}` has unused parameters: $unusedParameters")
    }
    return result
}