package com.example.scan.result

import com.example.ui.common.UIState

sealed class ScanResultUiState(
    val isLoading: Boolean = false,
    val loadingMessage: String = "Loading Image",
    val isRetry: Boolean = false,
    val retryMsg: String? = null,
    val isAutoRetry: Boolean = false,
    val autoRetryMsg: String? = null,
    val isDiscardingReceipt: Boolean = false,
    val isImageLoaded: Boolean = false,
    val imageUri: String? = null,
    val isMlResultReturned: Boolean = false,
    val mlResult: MLResult? = null,
) : UIState {
    object Loading : ScanResultUiState(isLoading = false)

    class RetryUiState(retryMsg: String? = null) :
        ScanResultUiState(isRetry = true, retryMsg = retryMsg)

    class AutoRetry(autoRetryMsg: String? = null) :
        ScanResultUiState(isAutoRetry = true, autoRetryMsg = autoRetryMsg)

    class ProcessingImage(imageUri: String?) : ScanResultUiState(
        imageUri = imageUri,
        isImageLoaded = true,
        isLoading = true,
        loadingMessage = "Processing Image"
    )

    class DialogUIState(
        isVisible: Boolean, imageUri: String?, mlResult: MLResult?, isMlResultReturned: Boolean
    ) : ScanResultUiState(
        imageUri = imageUri,
        isImageLoaded = true,
        isDiscardingReceipt = isVisible,
        mlResult = mlResult,
        isMlResultReturned = isMlResultReturned
    )

    class SaveImage(imageUri: String?, mlResult: MLResult?) : ScanResultUiState(
        imageUri = imageUri,
        isImageLoaded = true,
        isLoading = true,
        loadingMessage = "Saving Image",
        mlResult = mlResult,
    )

    class MLResultReceived(
        result: MLResult, imageUri: String?
    ) : ScanResultUiState(
        isMlResultReturned = true,
        imageUri = imageUri,
        mlResult = result,
    )
}