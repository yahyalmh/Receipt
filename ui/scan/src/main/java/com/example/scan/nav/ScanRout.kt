package com.example.scan.nav

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.*
import androidx.navigation.compose.composable
import com.example.scan.result.ScanResultScreen
import com.example.scan.camera.CameraScreen
import com.example.ui.common.ext.popCurrentTo
import javax.inject.Inject

/**
 * @author yaya (@yahyalmh)
 * @since 05th November 2022
 */

class ScanRout @Inject constructor(
    private val navController: NavHostController
) {
    fun popBackStack() = navController.popBackStack()

    fun navigateToCameraScreen() {
        navController.popCurrentTo(CAMERA_ROUTE)
    }

    fun navigateToScanResult(imageAdders: String) {
        val navOptions = NavOptions
            .Builder()
            .setPopUpTo(navController.currentDestination!!.route, true)
            .build()

        navController.navigate("$SCAN_RESULT_ROUT/$imageAdders", navOptions)
    }

    companion object {
        const val SCAN_ROUTE = "scan"
        private const val CAMERA_ROUTE = "camera"
        private const val SCAN_RESULT_ROUT = "scan_result"
        private const val IMAGE_FILE_PATH = "image_file_path"

        fun NavGraphBuilder.scanGraph() {
            navigation(route = SCAN_ROUTE, startDestination = CAMERA_ROUTE) {
                composable(route = CAMERA_ROUTE) { CameraScreen() }
                composable(
                    route = "$SCAN_RESULT_ROUT/{$IMAGE_FILE_PATH}",
                    arguments = listOf(navArgument(IMAGE_FILE_PATH) { type = NavType.StringType })
                ) {
                    ScanResultScreen()
                }
            }
        }
    }

    class ScanResultArgs(savedStateHandle: SavedStateHandle) {
        val imageAddress = savedStateHandle.get<String>(IMAGE_FILE_PATH).toString()
    }
}
