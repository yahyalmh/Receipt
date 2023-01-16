package com.example.setting.nav

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.setting.SettingScreen
import javax.inject.Inject

/**
 * @author yaya (@yahyalmh)
 * @since 05th November 2022
 */


class SettingRoute @Inject constructor(private val navController: NavHostController) {

    companion object {
        const val SETTING_ROUTE = "setting_route"

        fun NavGraphBuilder.settingGraph() {
            composable(route = SETTING_ROUTE) { SettingScreen() }
        }
    }
}
