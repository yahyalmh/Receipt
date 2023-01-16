package com.example.main.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.home.nav.HomeRoute.Companion.HOME_ROUTE
import com.example.home.nav.HomeRoute.Companion.homeGraph
import com.example.scan.nav.ScanRout.Companion.scanGraph
import com.example.setting.nav.SettingRoute.Companion.settingGraph

/**
 * @author yaya (@yahyalmh)
 * @since 29th October 2022
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = HOME_ROUTE
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        homeGraph()
        settingGraph()
        scanGraph()
    }
}