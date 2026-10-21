package org.hnau.commons.app.model.preferences

import arrow.core.Option
import kotlinx.coroutines.CoroutineScope

fun interface Preferences {

    operator fun get(
        key: String,
    ): Preference<Option<String>>

    interface Factory {

        suspend fun createPreferences(
            scope: CoroutineScope,
        ): Preferences
    }
}