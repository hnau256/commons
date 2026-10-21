package org.hnau.commons.gen.pipe.processor.ext

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import org.hnau.commons.gen.pipe.processor.data.Argument

internal val KSFunctionDeclaration.argumants: List<Argument>
    get() = parameters.mapIndexed { i, parameter ->
        val name = parameter
            .name
            ?: error("In function $log parameter #${i + 1} has no name")
        Argument(
            name = name.asString(),
            type = parameter.type.resolve(),
        )
    }