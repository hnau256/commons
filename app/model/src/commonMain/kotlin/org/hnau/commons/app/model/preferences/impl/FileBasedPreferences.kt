package org.hnau.commons.app.model.preferences.impl

import arrow.core.Option
import arrow.core.toOption
import org.hnau.commons.kotlin.coroutines.flow.state.mapState
import org.hnau.commons.kotlin.coroutines.flow.state.mutable.toMutableStateFlowAsInitial
import org.hnau.commons.kotlin.ifNull
import org.hnau.commons.kotlin.mapper.Mapper
import org.hnau.commons.kotlin.mapper.plus
import org.hnau.commons.kotlin.mapper.stringToStringsBySeparator
import org.hnau.commons.app.model.file.File
import org.hnau.commons.app.model.file.exists
import org.hnau.commons.app.model.file.mkDirs
import org.hnau.commons.app.model.file.parent
import org.hnau.commons.app.model.file.sink
import org.hnau.commons.app.model.file.source
import org.hnau.commons.app.model.preferences.Preference
import org.hnau.commons.app.model.preferences.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.io.buffered
import kotlinx.io.readString
import kotlinx.io.writeString

class FileBasedPreferences(
    private val scope: CoroutineScope,
    initialValues: Map<String, String>,
    private val updateValues: suspend (Map<String, String>) -> Unit,
) : Preferences {

    private val values: MutableStateFlow<Map<String, String>> =
        initialValues.toMutableStateFlowAsInitial()

    private val updateMutex = Mutex()

    override fun get(
        key: String,
    ): Preference<Option<String>> = Preference(
        value = values.mapState(scope) { values ->
            values[key].toOption()
        },
        update = { newValueOrNone ->
            updateMutex.withLock {
                val newValues = newValueOrNone.fold(
                    ifEmpty = {
                        values.value - key
                    },
                    ifSome = { newValue ->
                        values.value + (key to newValue)
                    }
                )
                updateValues(newValues)
                values.value = newValues
            }
        }
    )

    class Factory(
        private val preferencesFile: File,
    ) : Preferences.Factory {

        override suspend fun createPreferences(
            scope: CoroutineScope,
        ): Preferences {
            val text = withContext(Dispatchers.IO) {
                preferencesFile
                    .takeIf(File::exists)
                    ?.source()
                    ?.use { source -> source.buffered().readString() }

            }
            val initialValues = withContext(Dispatchers.Default) {
                text
                    ?.let(stringToValuesMapper.direct)
                    .orEmpty()
            }
            return FileBasedPreferences(
                scope = scope,
                initialValues = initialValues,
                updateValues = { newValues ->
                    val text = withContext(Dispatchers.Default) {
                        newValues.let(stringToValuesMapper.reverse)
                    }
                    withContext(Dispatchers.IO) {
                        preferencesFile
                            .apply { parent?.mkDirs() }
                            .sink()
                            .buffered()
                            .use { sink ->
                                sink.writeString(text)
                            }
                    }
                }
            )
        }

        companion object {

            private val stringToValuesMapper: Mapper<String, Map<String, String>> = run {

                val entryMapper: Mapper<String, Pair<String, String>> = ':'.let { separator ->
                    Mapper(
                        direct = { line ->
                            val separatorIndex = line.indexOf(separator)
                                .takeIf { it > 0 }
                                .ifNull { error("Unable parse key with value from '$line'") }
                            val key = line.substring(0, separatorIndex)
                            val value = line.substring(separatorIndex + 1)
                            key to value
                        },
                        reverse = { (key, value) ->
                            key + separator + value
                        }
                    )
                }

                val entriesMapper = Mapper<List<String>, Map<String, String>>(
                    direct = { it.associate(entryMapper.direct) },
                    reverse = { it.toList().map(entryMapper.reverse) }
                )

                val entriesStringsMapper: Mapper<String, List<String>> =
                    Mapper.stringToStringsBySeparator(separator = '\n')

                entriesStringsMapper + entriesMapper
            }
        }
    }
}