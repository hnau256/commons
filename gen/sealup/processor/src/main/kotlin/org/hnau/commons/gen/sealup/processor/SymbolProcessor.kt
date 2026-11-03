package org.hnau.commons.gen.sealup.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import org.hnau.commons.gen.sealup.processor.sealedinfo.CreateResult
import org.hnau.commons.gen.sealup.processor.sealedinfo.SealedInfo
import org.hnau.commons.gen.sealup.processor.sealedinfo.builder.create
import org.hnau.commons.gen.sealup.processor.sealedinfo.generator.generateCode

class SymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    override fun process(
        resolver: Resolver,
    ): List<KSAnnotated> = resolver
        .getSymbolsWithAnnotation(AnnotationInfo.nameWithPackage)
        .fold(initial = emptyList()) { deferred, annotated ->
            when (val result = SealedInfo.create(logger, annotated)) {
                is CreateResult.Success -> {
                    result.value.generateCode(codeGenerator)
                    deferred
                }

                is CreateResult.Deferred -> deferred + annotated
                is CreateResult.Error -> deferred
            }
        }
}
