package com.example.main

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.main.nav.AppNavHost
import com.example.home.nav.homeRoute
import com.example.main.theme.AppTheme
import com.example.setting.nav.settingRoute
import com.example.ui.common.ThemeType
import com.example.ui.common.component.bar.BottomAppBar
import com.example.ui.common.component.bar.BottomBarTab
import com.example.ui.common.component.bar.ConnectivityStatusView
import com.example.ui.main.R

/**
 * @author yaya (@yahyalmh)
 * @since 29th October 2022
 */
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState = viewModel.state.value

    val useDarkTheme = shouldUseDarkTheme(uiState.themeType)
    AppTheme(useDarkTheme = useDarkTheme) {
        ContentView(
            modifier = modifier,
            uiState = uiState,
            navController = navController,
            bottomBarTabs = bottomBarTabs()
        ) { tab ->
            viewModel.onEvent(MainUiEvent.ChangeTab(navController, tab))
        }
    }
}

@Composable
private fun ContentView(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    uiState: MainUiState,
    bottomBarTabs: List<BottomBarTab>,
    onNavigateToDestination: (BottomBarTab) -> Unit
) {
    Column {
        ConnectivityStatusView(
            modifier = modifier,
            isOnlineViewVisible = uiState.isOnlineViewVisible,
            isOfflineViewVisible = uiState.isOfflineViewVisible
        )

        Scaffold(
            modifier = modifier.fillMaxSize(),
            contentColor = MaterialTheme.colorScheme.surface,
            bottomBar = {
                AnimatedVisibility(visible = uiState.isBottomBarVisible) {
                    BottomAppBar(
                        modifier = Modifier.testTag(TestTag.BOTTOM_BAR),
                        tabs = bottomBarTabs,
                        onNavigateToDestination = onNavigateToDestination,
                        currentDestination = navController.currentBackStackEntryAsState().value?.destination
                    )
                }
            }
        ) { paddingValues ->
            SetupAppNavHost(navController, paddingValues)
        }
    }
}

@Composable
fun bottomBarTabs() = listOf(
    BottomBarTab(
        title = stringResource(id = R.string.home),
        route = homeRoute,
        selectedIcon = Icons.Default.Home,
        unselectedIcon = Icons.Default.Home,
        contentDescription = stringResource(id = R.string.homeTabContentDescription)
    ),

    BottomBarTab(
        title = stringResource(id = R.string.favorite),
        route = "",
        selectedIcon = Icons.Default.Favorite,
        unselectedIcon = Icons.Default.FavoriteBorder,
        contentDescription = stringResource(id = R.string.favoriteTabContentDescription)
    ),

    BottomBarTab(
        title = stringResource(id = R.string.setting),
        route = settingRoute,
        selectedIcon = Icons.Default.Settings,
        unselectedIcon = Icons.Default.Settings,
        contentDescription = stringResource(id = R.string.settingTabContentDescription)
    )
)

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun SetupAppNavHost(
    navHostController: NavHostController,
    padding: PaddingValues = PaddingValues.Absolute()
) {
    AppNavHost(
        navController = navHostController,
        modifier = Modifier
            .padding(padding)
            .consumedWindowInsets(padding)
    )
}

@Composable
fun shouldUseDarkTheme(themeType: ThemeType?): Boolean =
    when (themeType) {
        ThemeType.SYSTEM -> isSystemInDarkTheme()
        ThemeType.LIGHT -> false
        ThemeType.DARK -> true
        else -> isSystemInDarkTheme()
    }

@Composable
@Preview(
    showSystemUi = false,
    name = "OfflinePreview",
    device = Devices.PHONE
)
fun OfflineContentPreview() {
    val navController = rememberNavController()
    ContentView(
        uiState = MainUiState.Offline(),
        bottomBarTabs = bottomBarTabs(),
        navController = navController,
        onNavigateToDestination = {}
    )
}

@Composable
@Preview(
    showSystemUi = false,
    name = "OnlinePreview",
    device = Devices.PHONE,
    uiMode = UI_MODE_NIGHT_YES
)
fun OnlineContentPreview() {
    val navController = rememberNavController()
    ContentView(
        uiState = MainUiState.Online(),
        bottomBarTabs = bottomBarTabs(),
        navController = navController,
        onNavigateToDestination = {}
    )
}

