package org.hnau.commons.gen.pipe.processor

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class PipeSymbolProcessorProvider : SymbolProcessorProvider {

    override fun create(
        environment: SymbolProcessorEnvironment,
    ): SymbolProcessor = PipeSymbolProcessor(
        codeGenerator = environment.codeGenerator,
        logger = environment.logger,
    )
}