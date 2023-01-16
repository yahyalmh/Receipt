package com.example.scan.result

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.data.common.Result.Error
import com.example.data.common.Result.Success
import com.example.firestore.FirebaseInteractor
import com.example.mlkit.LanguageIdInteractor
import com.example.mlkit.TextRecognitionInteractor
import com.example.mlkit.TranslateInteractor
import com.example.scan.nav.ScanRout
import com.example.scan.result.ScanResultUiEvent.*
import com.example.scan.result.ScanResultUiState.*
import com.example.ui.common.BaseViewModel
import com.example.ui.common.SharedState
import com.example.ui.common.connectivity.ConnectivityMonitor
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class ScanResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val connectivityMonitor: ConnectivityMonitor,
    private val textRecognitionInteractor: TextRecognitionInteractor,
    private val languageIdInteractor: LanguageIdInteractor,
    private val translateInteractor: TranslateInteractor,
    private val firebaseInteractor: FirebaseInteractor,
    @ApplicationContext private val appContext: Context,
    private val scanRout: ScanRout,
) : BaseViewModel<ScanResultUiState, ScanResultUiEvent>(Loading) {
    private val scanResultArgs = ScanRout.ScanResultArgs(savedStateHandle)
    private val imageFile by lazy { File(appContext.filesDir, scanResultArgs.imageAddress) }

    init {
        processImage()
        setBottomBarVisibility(false)
    }

    private fun processImage() {
        viewModelScope.launch {
            val image = loadImage()
            setState(ProcessingImage(imageFile.absolutePath))
            textRecognitionInteractor.recognizeText(InputImage.fromBitmap(image, 0))
                .mapLatest {
                    val sourceLang = languageIdInteractor.identifyLanguage(text = it.text).first()
                    val translatedText =
                        translateInteractor.translate(it.text, sourceLang, "en").first()

                    MLResult(it, sourceLang, translatedText)
                }.onEach {
                    setState(MLResultReceived(it, state.value.imageUri))
                }.catch { e -> handleError(e) }.launchIn(viewModelScope)
        }
    }

    private fun handleError(e: Throwable) = setState(RetryUiState())

    private suspend fun loadImage(): Bitmap =
        withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            BitmapFactory.decodeFile(imageFile.absolutePath)
        }

    override fun onEvent(event: ScanResultUiEvent) {
        when (event) {
            Retry -> {}
            Retake -> handleRetake()
            DiscardReceipt -> finishScanning()
            is SaveReceipt -> saveReceipt(event.onImageSaved)
            DismissDialog -> setDialogState(isDialogVisible = false)
            NavigationBack -> setDialogState(isDialogVisible = true)
        }
    }

    private fun finishScanning() {
        setBottomBarVisibility(true)
        scanRout.popBackStack()
    }

    private fun handleRetake() {
        imageFile.delete()
        scanRout.navigateToCameraScreen()
    }

    private fun setDialogState(isDialogVisible: Boolean) {
        setState(
            DialogUIState(
                isVisible = isDialogVisible,
                imageUri = state.value.imageUri,
                mlResult = state.value.mlResult,
                isMlResultReturned = state.value.isMlResultReturned
            )
        )
    }

    private fun setBottomBarVisibility(isVisible: Boolean) {
        viewModelScope.launch {
            SharedState.bottomBarVisible.emit(isVisible)
        }
    }

    private fun saveReceipt(onImageSaved: () -> Unit) = viewModelScope.launch {
        setState(SaveImage(state.value.imageUri, mlResult = state.value.mlResult))
        state.value.mlResult?.let {
            firebaseInteractor.saveReceipts(it.toReceiptModel(imageFile.absolutePath))
                .onEach { result ->
                    when (result) {
                        is Success -> {
                            onImageSaved()
                            finishScanning()
                        }
                        is Error -> throw (result.exception ?: IOException("Error"))
                    }
                }.catch { e -> handleError(e) }.launchIn(viewModelScope)
        }
    }

    override fun onCleared() {
        imageFile.delete()
    }
}