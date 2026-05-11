package org.hnau.commons.app.projector.fractal.semantic.input.edit

import arrow.core.Either
import org.hnau.commons.app.projector.fractal.semantic.input.SInputMapper
import org.hnau.commons.app.projector.fractal.semantic.input.plus
import org.hnau.commons.app.projector.fractal.semantic.input.simplify

fun <EI, EO, VI, VO> SEditType<EI, VI>.addMapper(
    mapper: SInputMapper<VI, EO, VO>,
): SEditType<Either<EI, EO>, VO> = object : SEditType<Either<EI, EO>, VO> {

    private val source: SEditType<EI, VI>
        get() = this@addMapper
    override val config: SEditType.Config
        get() = source.config

    override val mapper: SInputMapper<String, Either<EI, EO>, VO> = source.mapper + mapper
}

fun <E, V> SEditType<Either<Nothing, E>, V>.simplify(): SEditType<E, V> =
    object : SEditType<E, V> {

        private val source: SEditType<Either<Nothing, E>, V>
            get() = this@simplify
        override val config: SEditType.Config
            get() = source.config

        override val mapper: SInputMapper<String, E, V> =
            source.mapper.simplify()
    }