package com.example.scan

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.data.common.Result.*
import com.example.data.common.model.dto.Photo
import com.example.data.common.model.dto.ReceiptModel
import com.example.firestore.FirebaseInteractor
import com.example.mlkit.LanguageIdInteractor
import com.example.mlkit.TextRecognitionInteractor
import com.example.mlkit.TranslateInteractor
import com.example.scan.ScanResultUiEvent.*
import com.example.scan.ScanResultUiState.*
import com.example.scan.nav.ScanResultArgs
import com.example.scan.util.SuperclassExclusionStrategy
import com.example.ui.common.BaseViewModel
import com.example.ui.common.SharedState
import com.example.ui.common.UIEvent
import com.example.ui.common.UIState
import com.example.ui.common.connectivity.ConnectivityMonitor
import com.google.gson.GsonBuilder
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.*
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
    @ApplicationContext val appContext: Context,
) : BaseViewModel<ScanResultUiState, ScanResultUiEvent>(Loading) {
    private val scanResultArgs = ScanResultArgs(savedStateHandle)
    private val imageFile by lazy { File(appContext.filesDir, scanResultArgs.imageFileName) }


    init {
        viewModelScope.launch {
            val image = loadImage()
            setState(ProcessingImage(imageFile.absolutePath))
            textRecognitionInteractor.recognizeText(InputImage.fromBitmap(image, 0)).mapLatest {
                val sourceLang = languageIdInteractor.identifyLanguage(text = it.text).first()
                val translatedText =
                    translateInteractor.translate(it.text, sourceLang, "en").first()
                MLResult(it, sourceLang, translatedText)
            }.onEach {
                setState(MLResultReceived(it, state.value.imageUri))
            }.catch { e -> handleError(e) }.launchIn(viewModelScope)
        }
        setBottomBarVisibility(false)
    }

    private fun handleError(e: Throwable) {
        setState(RetryUiState())
    }

    private suspend fun loadImage(): Bitmap {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            BitmapFactory.decodeFile(imageFile.absolutePath)
        }
    }


    private fun setBottomBarVisibility(isVisible: Boolean) {
        viewModelScope.launch {
            SharedState.bottomBarVisible.emit(isVisible)
        }
    }

    override fun onEvent(event: ScanResultUiEvent) {
        when (event) {
            Retry -> {}
            Retake -> imageFile.delete()
            NavigationBack -> setState(
                DialogUIState(
                    isVisible = true,
                    imageUri = state.value.imageUri,
                    mlResult = state.value.mlResult,
                    isMlResultReturned = state.value.isMlResultReturned
                )
            )
            DiscardReceipt -> setBottomBarVisibility(true)
            DismissDialog -> setState(
                DialogUIState(
                    isVisible = false,
                    imageUri = state.value.imageUri,
                    mlResult = state.value.mlResult,
                    isMlResultReturned = state.value.isMlResultReturned
                )
            )
            is SaveReceipt -> saveReceipt(event.onImageSaved)
        }
    }

    private fun saveReceipt(onImageSaved: () -> Unit) = viewModelScope.launch {
        setState(SaveImage(state.value.imageUri, mlResult = state.value.mlResult))
        state.value.mlResult?.let {
            firebaseInteractor.saveReceipts(it.toReceiptModel(imageFile.absolutePath))
                .onEach { result ->
                    when (result) {
                        is Success -> {
                            setBottomBarVisibility(true)
                            onImageSaved()
                        }
                        is Error -> throw(result.exception ?: IOException("Error"))
                    }
                }.catch { e -> handleError(e) }.launchIn(viewModelScope)
        }
    }

    override fun onCleared() {
        imageFile.delete()
    }
}

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

sealed interface ScanResultUiEvent : UIEvent {
    object Retry : ScanResultUiEvent
    object Retake : ScanResultUiEvent
    object NavigationBack : ScanResultUiEvent
    class SaveReceipt(val onImageSaved: () -> Unit) : ScanResultUiEvent
    object DiscardReceipt : ScanResultUiEvent
    object DismissDialog : ScanResultUiEvent
}

class MLResult(val text: Text, val languageId: String, val translate: String)

fun MLResult.toReceiptModel(photoLocalUri: String): ReceiptModel {
    val gsonBuilder = GsonBuilder().apply {
        addDeserializationExclusionStrategy(SuperclassExclusionStrategy())
        addSerializationExclusionStrategy(SuperclassExclusionStrategy())
    }
    return ReceiptModel(
        languageId = languageId,
        text = gsonBuilder.create().toJson(text),
        translate = translate,
        photo = Photo(
            localUri = photoLocalUri, timestamp = System.currentTimeMillis().toString()
        )
    )
}