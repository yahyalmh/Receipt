package com.example.home

import androidx.lifecycle.viewModelScope
import com.example.data.common.Result
import com.example.data.common.model.ExchangeRate
import com.example.data.common.model.dto.ReceiptModel
import com.example.firestore.FirebaseInteractor
import com.example.home.nav.HomeRoute
import com.example.ui.common.BaseViewModel
import com.example.ui.common.UIEvent
import com.example.ui.common.UIState
import com.example.ui.common.connectivity.ConnectivityMonitor
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject


/**
 * @author yaya (@yahyalmh)
 * @since 05th November 2022
 */

@HiltViewModel
open class HomeViewModel @Inject constructor(
    private val connectivityMonitor: ConnectivityMonitor,
    private val firebaseInteractor: FirebaseInteractor,
    private val homeRoute: HomeRoute,
) : BaseViewModel<HomeUiState, HomeUiEvent>(HomeUiState.Loading) {

    init {
        fetchReceipts()
        observeStorage()
    }

    private fun fetchReceipts() {
        viewModelScope.launch {
            firebaseInteractor.getAllReceipts()
                .onEach { handleData(it) }
                .catch { handleRetry(it) }
                .launchIn(viewModelScope)
        }
    }

    private fun observeStorage() {
        viewModelScope.launch {
            firebaseInteractor.observeFirestore()
                .onEach { handleData(it) }
                .catch { handleRetry(it) }
                .launchIn(viewModelScope)
        }
    }

    private fun handleData(result: Result<List<DocumentSnapshot>>) {
        when (result) {
            is Result.Success -> {
                result.data
                    .mapNotNull { document -> document.toObject(ReceiptModel::class.java) }
                    .sortedByDescending { it.photo?.timestamp }
                    .also {
                        setState(HomeUiState.Loaded(it))
                    }
            }
            is Result.Error -> throw (result.exception ?: IOException("Error"))
        }
    }

    private fun handleRetry(e: Throwable) = setState(HomeUiState.Retry(retryMsg = e.message))
    private fun handleAutoRetry(e: Throwable) = setState(HomeUiState.AutoRetry(e.message))
    override fun onEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.Retry -> setState(HomeUiState.Loading)
            HomeUiEvent.OnFabClick -> homeRoute.navigateToScan()
            else -> {}
        }
    }
}

sealed class HomeUiState(
    val isLoading: Boolean = false,
    val isRetry: Boolean = false,
    val retryMsg: String? = null,
    val isAutoRetry: Boolean = false,
    val autoRetryMsg: String? = null,
    val isLoaded: Boolean = false,
    val receipts: List<ReceiptModel> = emptyList(),
) : UIState {
    object Loading : HomeUiState(isLoading = true)

    class Retry(retryMsg: String? = null) : HomeUiState(isRetry = true, retryMsg = retryMsg)

    class AutoRetry(autoRetryMsg: String? = null) :
        HomeUiState(isAutoRetry = true, autoRetryMsg = autoRetryMsg)

    class Loaded(receipts: List<ReceiptModel>) : HomeUiState(isLoaded = true, receipts = receipts)
}

sealed interface HomeUiEvent : UIEvent {
    object Retry : HomeUiEvent
    object OnFabClick : HomeUiEvent
    class OnFavorite(val rate: ExchangeRate) : HomeUiEvent
}