package org.hnau.commons.app.projector.uikit.bubble

import kotlinx.coroutines.flow.StateFlow

interface BubblesProvider {

    val visibleBubble: StateFlow<Bubble?>
}
