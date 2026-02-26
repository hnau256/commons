package hnau.commons.gen.kotlin

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import hnau.commons.kotlin.ifNull

fun KSTypeReference.resolve(
    logger: KSPLogger,
): KSType? = resolve()
    .takeUnless(KSType::isError)
    .ifNull {
        logger.error("Unable resolve type reference", this)
        null
    }