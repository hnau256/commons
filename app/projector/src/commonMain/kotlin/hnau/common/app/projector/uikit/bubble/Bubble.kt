package hnau.common.app.projector.uikit.bubble

data class Bubble(
    val text: String,
    val duration: BubbleDuration = BubbleDuration.default,
)
