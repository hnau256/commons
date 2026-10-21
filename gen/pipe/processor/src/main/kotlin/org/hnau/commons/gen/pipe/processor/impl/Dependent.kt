package org.hnau.commons.gen.pipe.processor.impl

import com.google.devtools.ksp.symbol.KSName
import org.hnau.commons.gen.pipe.processor.data.Argument

internal sealed interface Dependent {

    val impl: KSName

    val arguments: List<Argument>
}