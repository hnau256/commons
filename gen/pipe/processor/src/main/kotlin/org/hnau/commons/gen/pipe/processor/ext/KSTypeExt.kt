package org.hnau.commons.gen.pipe.processor.ext

import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Nullability

internal fun commonType(
    a: KSType,
    b: KSType,
): KSType? = when {
    a.makeNullable() != b.makeNullable() -> null
    a.nullability != Nullability.NULLABLE -> a
    b.nullability != Nullability.NULLABLE -> b
    else -> a
}
