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
) : BaseViewModel<ScanResultUiState, ScanResultUiEvent>(Start) {
    private val scanResultArgs = ScanRout.ScanResultArgs(savedStateHandle)
    private val imageFile by lazy { File(appContext.filesDir, scanResultArgs.imageAddress) }

    init {
        setBottomBarVisibility(false)
        viewModelScope.launch {
            val image = loadImage()
            processImage(image)
        }
    }

    private suspend fun loadImage(): Bitmap {
        return withContext(Dispatchers.IO) {
            BitmapFactory.decodeFile(imageFile.absolutePath)
        }
    }

    private fun processImage(image: Bitmap) {
        textRecognitionInteractor
            .recognizeText(InputImage.fromBitmap(image, 0))
            .onStart { setState(ProcessImage(state.copy(imageAddress = imageFile.absolutePath))) }
            .mapLatest {
                val sourceLang = languageIdInteractor.identifyLanguage(text = it.text).first()
                val translatedText =
                    translateInteractor.translate(it.text, sourceLang, "en").first()

                MLResult(it, sourceLang, translatedText)
            }
            .onEach { setState(Loaded(state.copy(mlResult = it, isLoading = false))) }
            .catch { e -> handleError(e) }
            .launchIn(viewModelScope)
    }

    private fun handleError(e: Throwable) = setState(ScanResultUiState.Retry(state = state))

    private fun finishScanning() {
        setBottomBarVisibility(true)
        scanRout.popBackStack()
    }

    private fun handleRetake() {
        imageFile.delete()
        scanRout.navigateToCameraScreen()
    }

    private fun setBottomBarVisibility(isVisible: Boolean) {
        viewModelScope.launch {
            SharedState.bottomBarVisible.emit(isVisible)
        }
    }

    private fun saveReceipt(onImageSaved: () -> Unit) = viewModelScope.launch {
        state.mlResult?.let {
            firebaseInteractor
                .saveReceipts(it.toReceiptModel(imageFile.absolutePath))
                .onStart { setState(SaveImage(state)) }
                .onEach { result ->
                    when (result) {
                        is Success -> {
                            onImageSaved()
                            finishScanning()
                        }
                        is Error -> throw (result.exception ?: IOException("Error"))
                    }
                }
                .catch { e -> handleError(e) }
                .launchIn(viewModelScope)
        }
    }

    override fun onEvent(event: ScanResultUiEvent) {
        when (event) {
            ScanResultUiEvent.Retry -> {}
            Retake -> handleRetake()
            DiscardReceipt -> finishScanning()
            is SaveReceipt -> saveReceipt(event.onImageSaved)
            DismissDialog -> setState(Dialog(state.copy(isDialog = false)))
            NavigationBack -> setState(Dialog(state.copy(isDialog = true)))
        }
    }

    override fun onCleared() {
        imageFile.delete()
    }
}