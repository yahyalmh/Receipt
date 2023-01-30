package com.example.ui.common

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

/**
 * @author yaya (@yahyalmh)
 * @since 29th September 2022
 */

abstract class BaseViewModel<T : UIState, E : UIEvent>(initialState: T) : ViewModel() {
    private val internalSate: MutableState<T> = mutableStateOf(initialState)
    val state: T
        get() = internalSate.value

    abstract fun onEvent(event: E)

    protected fun setState(state: T) {
        internalSate.value = state
    }
}

interface UIState

interface UIEvent
