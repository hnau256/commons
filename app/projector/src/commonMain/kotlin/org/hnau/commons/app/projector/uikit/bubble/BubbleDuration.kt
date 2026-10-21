package org.hnau.commons.app.projector.uikit.bubble

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.jvm.JvmInline

@JvmInline
value class BubbleDuration(
    val duration: Duration,
) {

    companion object {

        val default = BubbleDuration(
            duration = 3.seconds,
        )
    }
}
