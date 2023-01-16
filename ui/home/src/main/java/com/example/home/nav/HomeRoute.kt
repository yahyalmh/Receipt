package com.example.home.nav

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.home.HomeScreen
import com.example.scan.nav.ScanRout.Companion.SCAN_ROUTE
import javax.inject.Inject

/**
 * @author yaya (@yahyalmh)
 * @since 05th November 2022
 */


class HomeRoute @Inject constructor(private val navController: NavHostController) {

    fun navigateToScan(navOptions: NavOptions? = null) {
        navController.navigate(SCAN_ROUTE, navOptions)
    }

    companion object {
        const val HOME_ROUTE = "home_route"

        fun NavGraphBuilder.homeGraph() {
            composable(route = HOME_ROUTE) { HomeScreen() }
        }
    }
}
