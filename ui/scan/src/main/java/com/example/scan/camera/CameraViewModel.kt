package com.example.scan.camera

import androidx.lifecycle.viewModelScope
import com.example.scan.camera.CameraUiState.*
import com.example.scan.nav.ScanRout
import com.example.ui.common.BaseViewModel
import com.example.ui.common.SharedState
import com.example.ui.common.UIEvent
import com.example.ui.common.UIState
import com.example.ui.common.connectivity.ConnectivityMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val connectivityMonitor: ConnectivityMonitor,
    private val scanRout: ScanRout,
) : BaseViewModel<CameraUiState, CameraUiEvent>(Start) {
    init {
        changeBottomBarVisibility(false)
    }

    private fun changeBottomBarVisibility(enabled: Boolean) {
        viewModelScope.launch { SharedState.bottomBarVisible.emit(enabled) }
    }

    override fun onEvent(event: CameraUiEvent) {
        when (event) {
            CameraUiEvent.Retry -> {}
            CameraUiEvent.PermissionGranted -> setState(Capturing)
            CameraUiEvent.NavigationBack -> {
                changeBottomBarVisibility(true)
                scanRout.popBackStack()
            }
            is CameraUiEvent.ImageCaptured -> scanRout.navigateToScanResult(event.imageAddress)
        }
    }

    private fun handleError(e: Throwable) {
        setState(Retry())
    }
}

sealed class CameraUiState(
    val isLoading: Boolean = false,
    val isRetry: Boolean = false,
    val retryMsg: String? = null,
    val isAutoRetry: Boolean = false,
    val autoRetryMsg: String? = null,
    val isPermissionGranted: Boolean = false,
) : UIState {

    class Retry(retryMsg: String? = null) : CameraUiState(isRetry = true, retryMsg = retryMsg)

    object Capturing : CameraUiState(isPermissionGranted = true)

    object Start : CameraUiState(isPermissionGranted = false)
}

sealed interface CameraUiEvent : UIEvent {
    object Retry : CameraUiEvent
    object PermissionGranted : CameraUiEvent
    object NavigationBack : CameraUiEvent
    class ImageCaptured(val imageAddress: String) : CameraUiEvent
}