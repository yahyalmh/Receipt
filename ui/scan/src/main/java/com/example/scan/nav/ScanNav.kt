package com.example.scan.nav

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import androidx.navigation.compose.composable
import com.example.scan.ScanResultScreen
import com.example.scan.ScanScreen

/**
 * @author yaya (@yahyalmh)
 * @since 05th November 2022
 */


const val scanRoute = "scan_route"
fun NavController.navigateToScan(navOptions: NavOptions? = null) {
    this.navigate(scanRoute, navOptions)
}

const val scanResultRoute = "scan_result_route"
const val imageFileNameArgsKey = "image_file_name"

class ScanResultArgs(savedStateHandle: SavedStateHandle) {
    val imageFileName = savedStateHandle.get<String>(imageFileNameArgsKey).toString()
}

fun NavController.navigateToScanResult(imageFileName: String, navOptions: NavOptions? = null) {
    this.navigate("$scanResultRoute/$imageFileName", navOptions)
}

fun NavGraphBuilder.scanGraph(navController: NavHostController) {
    composable(route = scanRoute) {
        ScanScreen(navController = navController)
    }
    composable(
        route = "$scanResultRoute/{$imageFileNameArgsKey}",
        arguments = listOf(navArgument(imageFileNameArgsKey) { type = NavType.StringType })
    ) {
        ScanResultScreen(navController = navController)
    }
}