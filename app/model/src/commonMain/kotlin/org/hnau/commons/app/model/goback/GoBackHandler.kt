package org.hnau.commons.app.model.goback

import kotlinx.coroutines.flow.StateFlow

typealias GoBackHandler = StateFlow<(() -> Unit)?>
