package hnau.commons.kotlin.coroutines.flow.state.mutable

import kotlinx.coroutines.flow.MutableStateFlow

fun <T> T.toMutableStateFlowAsInitial(): MutableStateFlow<T> =
    MutableStateFlow(this)