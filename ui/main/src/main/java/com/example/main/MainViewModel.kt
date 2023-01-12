package com.example.main

import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.navOptions
import com.example.datastore.DatastoreInteractor
import com.example.home.nav.homeRoute
import com.example.home.nav.navigateToHome
import com.example.setting.nav.navigateToSetting
import com.example.setting.nav.settingRoute
import com.example.ui.common.*
import com.example.ui.common.component.bar.BottomBarTab
import com.example.ui.common.connectivity.ConnectivityMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val connectivityMonitor: ConnectivityMonitor,
    private val datastoreInteractor: DatastoreInteractor,
) : BaseViewModel<MainUiState, MainUiEvent>(
    MainUiState.HideNetStatusView()
) {
    private var isAppLaunchedForFirstTime: Boolean = true

    init {
        observeAppTheme()
        observeBottomBarState()
        observeConnectivityState()
    }

    private fun observeBottomBarState() {
        SharedState.bottomBarVisible
            .distinctUntilChanged()
            .onEach {
                setState(
                    MainUiState.BottomVisibleChange(
                        state.value.isOnlineViewVisible,
                        state.value.isOfflineViewVisible,
                        isBottomBarVisible = it,
                        themeType = state.value.themeType
                    )
                )
            }.launchIn(viewModelScope)
    }

    private fun observeAppTheme() {
        datastoreInteractor
            .getThemeType()
            .onEach {
                val themeType = it?.toThemeType()
                setState(
                    MainUiState.SetAppTheme(
                        state.value.isOnlineViewVisible,
                        state.value.isOfflineViewVisible,
                        themeType
                    )
                )
            }
            .launchIn(viewModelScope)

    }

    private fun observeConnectivityState() {
        connectivityMonitor.isOnline
            .distinctUntilChanged()
            .onEach { isOnline ->
                if (isOnline) {
                    handelOnlineState()
                } else {
                    setState(MainUiState.Offline(state.value.themeType))
                }
            }.launchIn(viewModelScope)
    }

    private fun handelOnlineState() {
        if (isAppLaunchedForFirstTime) {
            isAppLaunchedForFirstTime = false
            return
        }
        setState(MainUiState.Online(state.value.themeType))
        hideOnlineViewAfterWhile()
    }

    private fun hideOnlineViewAfterWhile() {
        val hideOnlineViewDelay: Long = 2000
        viewModelScope.launch {
            delay(hideOnlineViewDelay)
            setState(MainUiState.HideNetStatusView(state.value.themeType))
        }
    }

    override fun onEvent(event: MainUiEvent) {
        when (event) {
            is MainUiEvent.ChangeTab -> changeBottomBarDestination(
                event.navController,
                destination = event.destination
            )
        }
    }

    private fun changeBottomBarDestination(
        navController: NavController,
        destination: BottomBarTab
    ) {
        val id = navController.graph.findStartDestination().id
        val navOptions = navOptions {
            popUpTo(id) {
//                saveState = true
            }
            launchSingleTop = true
//            restoreState = true
        }

        when (destination.route) {
            homeRoute -> navController.navigateToHome(navOptions)
            settingRoute -> navController.navigateToSetting(navOptions)
        }
    }
}

sealed interface MainUiEvent : UIEvent {
    class ChangeTab(val navController: NavController, val destination: BottomBarTab) : MainUiEvent
}

sealed class MainUiState(
    val isOnlineViewVisible: Boolean = false,
    val isOfflineViewVisible: Boolean = false,
    val themeType: ThemeType? = ThemeType.SYSTEM,
    val isBottomBarVisible: Boolean = true,
) : UIState {

    object None : MainUiState(false, false)

    class HideNetStatusView(
        themeType: ThemeType? = null
    ) : MainUiState(
        isOnlineViewVisible = false,
        isOfflineViewVisible = false,
        themeType = themeType
    )

    class Offline(
        themeType: ThemeType? = null
    ) : MainUiState(isOfflineViewVisible = true, themeType = themeType)

    class Online(
        themeType: ThemeType? = null
    ) : MainUiState(isOnlineViewVisible = true, themeType = themeType)

    class BottomVisibleChange(
        isOnlineViewVisible: Boolean,
        isOfflineViewVisible: Boolean,
        isBottomBarVisible: Boolean,
        themeType: ThemeType? = null
    ) : MainUiState(
        isOnlineViewVisible = isOnlineViewVisible,
        isOfflineViewVisible = isOfflineViewVisible,
        isBottomBarVisible = isBottomBarVisible,
        themeType = themeType
    )

    class SetAppTheme(
        isOnlineViewVisible: Boolean,
        isOfflineViewVisible: Boolean,
        themeType: ThemeType? = null
    ) : MainUiState(
        isOnlineViewVisible = isOnlineViewVisible,
        isOfflineViewVisible = isOfflineViewVisible,
        themeType = themeType
    )
}

