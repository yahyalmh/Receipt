package com.example.scan.result

import com.example.ui.common.UIState

open class ScanResultUiState(
    val isLoading: Boolean = false,
    val isDialogVisisble: Boolean = false,
    val loadingMessage: String? = null,
    val retryMessage: String? = null,
    val autoRetryMessage: String? = null,
    val imageAddress: String? = null,
    val mlResult: MLResult? = null,
) : UIState {
    val isData: Boolean
        get() = !this.isRetry && !this.isAutoRetry
    val isRetry: Boolean
        get() = this is Retry
    val isAutoRetry: Boolean
        get() = this is AutoRetry

    constructor(state: ScanResultUiState) : this(
        state.isLoading,
        state.isDialogVisisble,
        state.loadingMessage,
        state.retryMessage,
        state.autoRetryMessage,
        state.imageAddress,
        state.mlResult
    )

    fun copy(
        isLoading: Boolean = this.isLoading,
        isDialog: Boolean = this.isDialogVisisble,
        loadingMessage: String? = this.loadingMessage,
        retryMessage: String? = this.retryMessage,
        autoRetryMessage: String? = this.autoRetryMessage,
        imageAddress: String? = this.imageAddress,
        mlResult: MLResult? = this.mlResult,
    ) = ScanResultUiState(
        isLoading = isLoading,
        isDialogVisisble = isDialog,
        loadingMessage = loadingMessage,
        retryMessage = retryMessage,
        autoRetryMessage = autoRetryMessage,
        imageAddress = imageAddress,
        mlResult = mlResult
    )

    object Start : ScanResultUiState()
    class Retry(state: ScanResultUiState) : ScanResultUiState(state)
    class AutoRetry(state: ScanResultUiState) : ScanResultUiState(state)
    class Dialog(state: ScanResultUiState) : ScanResultUiState(state)
    class Loaded(state: ScanResultUiState) : ScanResultUiState(state)
    class ProcessImage(state: ScanResultUiState) : ScanResultUiState(
        state.copy(
            isLoading = true,
            loadingMessage = "Processing Image"
        )
    )

    class SaveImage(state: ScanResultUiState) : ScanResultUiState(
        state.copy(
            isLoading = true,
            loadingMessage = "Saving Image"
        )
    )
}