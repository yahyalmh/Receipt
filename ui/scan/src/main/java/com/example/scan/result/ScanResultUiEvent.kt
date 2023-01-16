package com.example.scan.result

import com.example.ui.common.UIEvent

sealed interface ScanResultUiEvent : UIEvent {
    object Retry : ScanResultUiEvent
    object Retake : ScanResultUiEvent
    object NavigationBack : ScanResultUiEvent
    class SaveReceipt(val onImageSaved: () -> Unit) : ScanResultUiEvent
    object DiscardReceipt : ScanResultUiEvent
    object DismissDialog : ScanResultUiEvent
}

