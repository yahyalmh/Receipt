package com.example.main

import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.datastore.DatastoreInteractor
import com.example.main.MainUiState.BottomVisibleChange
import com.example.main.MainUiState.NetStatus
import com.example.main.MainUiState.SetAppTheme
import com.example.main.MainUiState.Start
import com.example.main.nav.MainRout
import com.example.ui.common.BaseViewModel
import com.example.ui.common.SharedState
import com.example.ui.common.ThemeType
import com.example.ui.common.UIEvent
import com.example.ui.common.UIState
import com.example.ui.common.component.bar.BottomBarTab
import com.example.ui.common.connectivity.ConnectivityMonitor
import com.example.ui.common.toThemeType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val connectivityMonitor: ConnectivityMonitor,
    private val datastoreInteractor: DatastoreInteractor,
    val navController: NavHostController,
    private val mainRout: MainRout,
) : BaseViewModel<MainUiState, MainUiEvent>(Start) {
    private var isAppLaunchedForFirstTime: Boolean = true

    init {
        observeAppTheme()
        observeBottomBarState()
        observeConnectivityState()
    }

    private fun observeBottomBarState() =
        SharedState.bottomBarVisible
            .distinctUntilChanged()
            .onEach { setState(BottomVisibleChange(state.copy(isBottomBarVisible = it))) }
            .launchIn(viewModelScope)

    private fun observeAppTheme() {
        datastoreInteractor
            .getThemeType()
            .onEach { setState(SetAppTheme(state.copy(themeType = it?.toThemeType()))) }
            .launchIn(viewModelScope)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeConnectivityState() {
        connectivityMonitor.isOnline
            .distinctUntilChanged()
            .mapLatest { isOnline ->
                val shouldShowNetStatus = (isAppLaunchedForFirstTime && isOnline).not()
                setState(
                    NetStatus(
                        state.copy(
                            isOnline = isOnline,
                            isStatusVisible = shouldShowNetStatus
                        )
                    )
                )
                isAppLaunchedForFirstTime = false
            }.launchIn(viewModelScope)
    }

    override fun onEvent(event: MainUiEvent) {
        when (event) {
            is MainUiEvent.ChangeTab -> mainRout.setBottomBarDestination(event.destination)
        }
    }
}

sealed interface MainUiEvent : UIEvent {
    class ChangeTab(val destination: BottomBarTab) : MainUiEvent
}

open class MainUiState(
    val themeType: ThemeType? = ThemeType.SYSTEM,
    val isBottomBarVisible: Boolean = true,
    val isOnline: Boolean = false,
    val isConnectivityStatusVisible: Boolean = false,
) : UIState {

    constructor(state: MainUiState) : this(
        state.themeType,
        state.isBottomBarVisible,
        state.isOnline,
        state.isConnectivityStatusVisible
    )

    fun copy(
        themeType: ThemeType? = this.themeType,
        isBottomBarVisible: Boolean = this.isBottomBarVisible,
        isOnline: Boolean = this.isOnline,
        isStatusVisible: Boolean = this.isConnectivityStatusVisible,
    ) = MainUiState(
        themeType = themeType,
        isBottomBarVisible = isBottomBarVisible,
        isOnline = isOnline,
        isConnectivityStatusVisible = isStatusVisible,
    )

    object Start : MainUiState()
    class NetStatus(state: MainUiState) : MainUiState(state)
    class BottomVisibleChange(state: MainUiState) : MainUiState(state)
    class SetAppTheme(state: MainUiState) : MainUiState(state)
}

