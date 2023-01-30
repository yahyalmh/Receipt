package com.example.home

import androidx.lifecycle.viewModelScope
import com.example.data.common.Result
import com.example.data.common.model.dto.ReceiptModel
import com.example.firestore.FirebaseInteractor
import com.example.home.HomeUiState.Empty
import com.example.home.HomeUiState.Loaded
import com.example.home.HomeUiState.Loading
import com.example.home.HomeUiState.Retry
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
import java.util.*
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
) : BaseViewModel<HomeUiState, HomeUiEvent>(Loading) {

    init {
        fetchReceipts()
        observeStorage()
    }

    private fun fetchReceipts() {
        Currency.getAvailableCurrencies()
        firebaseInteractor.getAllReceipts()
            .onEach { handleData(it) }
            .catch { handleRetry(it) }
            .launchIn(viewModelScope)
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
                        if (it.isEmpty()) {
                            setState(Empty)
                        } else {
                            setState(Loaded(state.copy(receipts = it)))
                        }
                    }
            }
            is Result.Error -> throw (result.exception ?: IOException("Error"))
        }
    }

    private fun handleRetry(e: Throwable) = setState(Retry(state.copy(retryMessage = e.message)))
    private fun handleAutoRetry(e: Throwable) = setState(state.copy(autoRetryMessage = e.message))
    override fun onEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.Retry -> setState(Loading)
            HomeUiEvent.OnFabClick -> homeRoute.navigateToScan()
            else -> {}
        }
    }
}

open class HomeUiState(
    val retryMessage: String? = null,
    val autoRetryMessage: String? = null,
    val receipts: List<ReceiptModel> = emptyList(),
) : UIState {
    val isLoading: Boolean
        get() = this is Loading
    val isRetry: Boolean
        get() = this is Retry
    val isAutoRetry: Boolean
        get() = this is AutoRetry
    val isLoaded: Boolean
        get() = this is Loaded
    val isEmpty: Boolean
        get() = this is Empty

    constructor(state: HomeUiState) : this(
        retryMessage = state.retryMessage,
        autoRetryMessage = state.autoRetryMessage,
        receipts = state.receipts
    )

    fun copy(
        retryMessage: String? = this.retryMessage,
        autoRetryMessage: String? = this.autoRetryMessage,
        receipts: List<ReceiptModel> = this.receipts
    ) = HomeUiState(
        retryMessage = retryMessage,
        autoRetryMessage = autoRetryMessage,
        receipts = receipts
    )


    object Loading : HomeUiState()
    object Empty : HomeUiState()
    class Retry(state: HomeUiState) : HomeUiState(state)
    class AutoRetry(state: HomeUiState) : HomeUiState(state)
    class Loaded(state: HomeUiState) : HomeUiState(state)
}

sealed interface HomeUiEvent : UIEvent {
    object Retry : HomeUiEvent
    object OnFabClick : HomeUiEvent
    object OnFavorite : HomeUiEvent
}