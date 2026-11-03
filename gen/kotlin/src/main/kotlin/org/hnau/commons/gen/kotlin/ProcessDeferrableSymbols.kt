package org.hnau.commons.gen.kotlin

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.validate

data class DeferrableSymbolsResult<T>(
    val values: List<T> = emptyList(),
    val deferred: List<KSAnnotated> = emptyList(),
)

fun <T : Any> processDeferrableSymbols(
    resolver: Resolver,
    annotationName: String,
    create: (KSAnnotated) -> T?,
): DeferrableSymbolsResult<T> = resolver
    .getSymbolsWithAnnotation(annotationName)
    .fold(initial = DeferrableSymbolsResult()) { result, annotated ->
        when {
            !annotated.validate() -> result.copy(
                deferred = result.deferred + annotated,
            )

            else -> create(annotated)
                ?.let { value ->
                    result.copy(
                        values = result.values + value,
                    )
                }
                ?: result
        }
    }
