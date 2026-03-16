package org.hnau.commons.gen.pipe.processor.impl

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import org.hnau.commons.gen.pipe.processor.data.Argument
import org.hnau.commons.gen.pipe.processor.utils.PipeAnnotationClassInfo

internal fun InterfaceToImplement.Companion.create(
    annotated: KSAnnotated,
): InterfaceToImplement {

    val interfaceToImplement = (annotated as? KSClassDeclaration)
        ?.takeIf { it.classKind == ClassKind.INTERFACE }
        ?: error("@${PipeAnnotationClassInfo.simpleName} annotation suitable only for interfaces, got $annotated")

    return InterfaceToImplement(
        file = annotated.containingFile!!,
        declaration = interfaceToImplement,
        properties = interfaceToImplement
            .getAllProperties()
            .fold(
                initial = emptyList(),
            ) { acc, property ->
                acc + property.toProperty(
                    interfaceClassInfo = interfaceToImplement,
                    alreadyCollectedProperties = acc
                )
            },
        factoryMethods = interfaceToImplement
            .getAllFunctions()
            .filter(KSFunctionDeclaration::isFactoryMethod)
            .map { function ->
                function.toFactoryMethod(
                    interfaceClassInfo = interfaceToImplement,
                )
            }
            .toList(),
        hasCompanionObject = interfaceToImplement
            .declarations
            .any { it is KSClassDeclaration && it.isCompanionObject },
    )
}

private val builtInFunctionsNames: Set<String> = setOf("equals", "toString", "hashCode")

private val KSFunctionDeclaration.isFactoryMethod: Boolean
    get() = simpleName.asString() !in builtInFunctionsNames

private fun KSPropertyDeclaration.toProperty(
    interfaceClassInfo: KSDeclaration,
    alreadyCollectedProperties: List<Argument>,
): Argument {
    val name = simpleName.asString()
    val type = type.resolve()
    val previousPropertyWithSameType = alreadyCollectedProperties.firstOrNull { it.type == type }
    if (previousPropertyWithSameType != null) {
        error("Properties '${previousPropertyWithSameType.name}' and '$name' of interface ${interfaceClassInfo.qualifiedName?.asString()} has same type ${type.declaration.qualifiedName?.asString()}")
    }
    return Argument(
        name = name,
        type = type,
    )
}

private fun KSFunctionDeclaration.toFactoryMethod(
    interfaceClassInfo: KSDeclaration,
): InterfaceToImplement.FactoryMethod {

    val functionLogString = "function '${qualifiedName?.asString()}'"

    val returnType = returnType!!.resolve()

    val returnDeclaration = returnType.declaration

    if (returnDeclaration !is KSClassDeclaration) {
        error("Return type of $functionLogString must be class, got ${returnDeclaration.qualifiedName?.asString()} with annotations: $annotations")
    }

    val name = simpleName.asString()
    return InterfaceToImplement.FactoryMethod(
        name = name,
        result = returnType,
        parameters = parameters.fold(
            initial = emptyList(),
        ) { acc, parameter ->
            acc + parameter.toFactoryMethodArgument(
                declaration = interfaceClassInfo,
                factoryMethodName = name,
                previousArguments = acc
            )
        },
    )
}

private fun KSValueParameter.toFactoryMethodArgument(
    declaration: KSDeclaration,
    factoryMethodName: String,
    previousArguments: List<Argument>,
): Argument {
    val name = name!!.asString()
    val type = type.resolve()
    val previousArgumentWithSameType = previousArguments.firstOrNull { it.type == type }
    if (previousArgumentWithSameType != null) {
        error("Arguments '${previousArgumentWithSameType.name}' and '$name' of factory method `$factoryMethodName` of interface ${declaration.qualifiedName?.asString()} has same type ${type.declaration.qualifiedName?.asString()}")
    }
    return Argument(
        name = name,
        type = type,
    )
}