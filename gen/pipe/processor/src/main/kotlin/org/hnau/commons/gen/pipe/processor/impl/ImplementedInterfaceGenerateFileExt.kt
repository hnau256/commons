package org.hnau.commons.gen.pipe.processor.impl

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeVariableName
import com.squareup.kotlinpoet.ksp.writeTo
import org.hnau.commons.gen.pipe.processor.ext.packageName

private fun KSName.toClassName(): ClassName = ClassName(
    packageName = packageName,
    simpleNames = getShortName().split("."),
)

fun TypeParameterResolver(
    parametersMap: Map<String, TypeVariableName>,
): TypeParameterResolver = object : TypeParameterResolver {
    override val parametersMap: Map<String, TypeVariableName> = parametersMap
    override fun get(index: String): TypeVariableName = parametersMap.getValue(index)
}

private fun KSTypeParameter.toTypeVariableNameLocal(): TypeVariableName {
    return toTypeVariableName(
        typeParamResolver = TypeParameterResolver(
            parametersMap = typeParameters.associate { typeParameter ->
                typeParameter.name.getQualifier() to typeParameter.toTypeVariableNameLocal()

            }
        )
    )
}

internal fun ImplementedInterface.generateFile(
    codeGenerator: CodeGenerator,
) {

    val interfaceClass = interfaceToImplement.declaration.toClassName()
    val packageName = interfaceClass.packageName
    val implClass = impl.toClassName()
    val a = buildList {
        add(interfaceToImplement.declaration)
        addAll(
            factoryMethods.map { it.result.declaration }
        )
    }
    val implTypeParametersResolver =
        TypeParameterResolver(a.flatMap { it.typeParameters }.associate { typeParameter ->
            typeParameter.name.asString() to typeParameter.toTypeVariableName()
        }
        )
    FileSpec
        .builder(
            packageName = packageName,
            fileName = implClass.simpleName,
        )
        .addType(
            TypeSpec
                .classBuilder(implClass)
                .apply {
                    if (arguments.isNotEmpty()) {
                        addModifiers(KModifier.DATA)
                    }
                }
                .addTypeVariables(
                    interfaceToImplement.declaration.typeParameters.map { typeParameter ->
                        typeParameter.toTypeVariableName(implTypeParametersResolver)
                    }
                )
                .primaryConstructor(
                    FunSpec
                        .constructorBuilder()
                        .addParameters(
                            arguments.map { argument ->
                                try {
                                    ParameterSpec(
                                        name = argument.name,
                                        type = argument.type.toTypeName(implTypeParametersResolver),
                                    )
                                } catch (th: Throwable) {
                                    throw IllegalStateException(
                                        "Argument: ${argument}. Resolver: ${implTypeParametersResolver.parametersMap}. Class: $implClass",
                                        th
                                    )
                                }
                            }
                        )
                        .build()
                )
                .addProperties(
                    overrideArguments.map { argument ->
                        PropertySpec
                            .builder(
                                name = argument.name,
                                type = argument.type.toTypeName(implTypeParametersResolver),
                            )
                            .initializer(argument.name)
                            .addModifiers(KModifier.OVERRIDE)
                            .build()
                    }
                )
                .addProperties(
                    privateArguments.map { argument ->
                        PropertySpec
                            .builder(
                                name = argument.name,
                                type = argument.type.toTypeName(implTypeParametersResolver),
                            )
                            .initializer(argument.name)
                            .addModifiers(KModifier.PRIVATE)
                            .build()
                    }
                )
                .addSuperinterface(
                    interfaceClass.run {
                        val typeArguments =
                            interfaceToImplement.declaration.typeParameters.map { typeParameter ->
                                typeParameter.toTypeVariableName(implTypeParametersResolver)
                            }.takeIf { it.isNotEmpty() }
                        when (typeArguments) {
                            null -> this
                            else -> parameterizedBy(typeArguments)
                        }
                    }
                )
                .addFunctions(
                    factoryMethods.map { method ->
                        FunSpec
                            .builder(method.name)
                            .addModifiers(KModifier.OVERRIDE)
                            .addParameters(
                                method.arguments.map { argument ->
                                    ParameterSpec(
                                        name = argument.name,
                                        type = argument.type.toTypeName(),
                                    )
                                }
                            )
                            .returns(method.result.toTypeName())
                            .addCode(
                                CodeBlock
                                    .builder()
                                    .add("return %T(\n", method.resultImpl.toClassName())
                                    .apply {
                                        method.dependentConstructorArguments.forEach { (key, value) ->
                                            add("%N = %N,\n", key, value)
                                        }
                                    }
                                    .add(")")
                                    .build()
                            )
                            .build()
                    }
                )
                .build()
        )
        .apply {
            if (interfaceToImplement.hasCompanionObject) {
                addFunction(
                    FunSpec
                        .builder("impl")
                        .receiver(interfaceClass.nestedClass("Companion"))
                        .addParameters(
                            arguments.map { argument ->
                                ParameterSpec(
                                    name = argument.name,
                                    type = argument.type.toTypeName(implTypeParametersResolver),
                                )
                            }
                        )
                        .returns(interfaceClass)
                        .addCode(
                            CodeBlock
                                .builder()
                                .add("return %T(\n", impl.toClassName())
                                .apply {
                                    arguments.forEach { (key) ->
                                        add("%N = %N,\n", key, key)
                                    }
                                }
                                .add(")")
                                .build()
                        )
                        .build()
                )
            }
        }
        .build()
        .writeTo(
            codeGenerator = codeGenerator,
            aggregating = true,
            originatingKSFiles = listOf(interfaceToImplement.file),
        )
}