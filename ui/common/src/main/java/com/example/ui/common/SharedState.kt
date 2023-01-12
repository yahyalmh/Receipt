package com.example.ui.common

import kotlinx.coroutines.flow.MutableSharedFlow

object SharedState {
    val bottomBarVisible = MutableSharedFlow<Boolean>()

}