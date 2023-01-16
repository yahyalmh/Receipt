package com.example.main.nav

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import com.example.home.nav.HomeRoute.Companion.HOME_ROUTE
import com.example.setting.nav.SettingRoute.Companion.SETTING_ROUTE
import com.example.ui.common.component.bar.BottomBarTab
import javax.inject.Inject

/**
 * @author yaya (@yahyalmh)
 * @since 05th November 2022
 */

class MainRout @Inject constructor(
    private val navController: NavHostController
) {
    fun setBottomBarDestination(destination: BottomBarTab) {
        val id = navController.graph.findStartDestination().id
        val navOptions = navOptions {
            popUpTo(id) // { saveState = true }
            launchSingleTop = true
//            restoreState = true
        }

        when (destination.route) {
            HOME_ROUTE -> navigateToHome(navOptions)
            SETTING_ROUTE -> navigateToSetting(navOptions)
        }
    }

    private fun navigateToHome(navOptions: NavOptions? = null) {
        navController.navigate(HOME_ROUTE, navOptions)
    }

    private fun navigateToSetting(navOptions: NavOptions? = null) {
        navController.navigate(SETTING_ROUTE, navOptions)
    }
}
