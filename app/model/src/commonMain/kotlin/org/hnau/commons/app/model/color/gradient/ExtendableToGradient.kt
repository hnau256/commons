package org.hnau.commons.app.model.color.gradient

interface ExtendableToGradient<C> {

    fun step(
        color: C,
        weight: Float = 1f,
    ): Gradient<C>
}
