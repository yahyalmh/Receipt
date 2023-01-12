package com.example.setting.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.setting.SettingScreen

const val settingRoute = "setting_route"

fun NavController.navigateToSetting(navOptions: NavOptions? = null) {
    this.navigate(settingRoute, navOptions)
}

fun NavGraphBuilder.settingGraph(navController: NavHostController) {
    composable(route = settingRoute) {
        SettingScreen(navController = navController)
    }
}