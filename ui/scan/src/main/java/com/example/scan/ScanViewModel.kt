package com.example.scan

import androidx.lifecycle.viewModelScope
import com.example.ui.common.BaseViewModel
import com.example.ui.common.SharedState
import com.example.ui.common.UIEvent
import com.example.ui.common.UIState
import com.example.ui.common.connectivity.ConnectivityMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val connectivityMonitor: ConnectivityMonitor,
) : BaseViewModel<ScanUiState, ScanUiEvent>(ScanUiState.Start) {
    init {
        changeBottomBarVisibility(false)
    }

    private fun changeBottomBarVisibility(enabled: Boolean) {
        viewModelScope.launch { SharedState.bottomBarVisible.emit(enabled) }
    }

    override fun onEvent(event: ScanUiEvent) {
        when (event) {
            ScanUiEvent.Retry -> {}
            ScanUiEvent.PermissionGranted -> setState(ScanUiState.Capturing)
            ScanUiEvent.NavigationBack -> changeBottomBarVisibility(true)
        }
    }
    private fun handleError(e: Throwable) {
        setState(ScanUiState.Retry())
    }
}

sealed class ScanUiState(
    val isLoading: Boolean = false,
    val isRetry: Boolean = false,
    val retryMsg: String? = null,
    val isAutoRetry: Boolean = false,
    val autoRetryMsg: String? = null,
    val isPermissionGranted: Boolean = false,
) : UIState {
    object Loading : ScanUiState(isLoading = true)

    class Retry(retryMsg: String? = null) : ScanUiState(isRetry = true, retryMsg = retryMsg)

    class AutoRetry(autoRetryMsg: String? = null) :
        ScanUiState(isAutoRetry = true, autoRetryMsg = autoRetryMsg)

    object Capturing : ScanUiState(isPermissionGranted = true)

    object Start : ScanUiState(isPermissionGranted = false)
}

sealed interface ScanUiEvent : UIEvent {
    object Retry : ScanUiEvent
    object PermissionGranted : ScanUiEvent
    object NavigationBack : ScanUiEvent
}