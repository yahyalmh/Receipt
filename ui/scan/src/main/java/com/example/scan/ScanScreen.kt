package com.example.scan

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.example.scan.nav.navigateToScanResult
import com.example.ui.common.component.icon.AppIcons
import com.example.ui.common.component.screen.TopBarScaffold
import com.example.ui.common.component.view.AutoRetryView
import com.example.ui.common.component.view.LoadingView
import com.example.ui.common.component.view.PermissionView
import com.example.ui.common.component.view.RetryView
import com.example.ui.scan.R
import com.google.mlkit.vision.common.InputImage

@Composable
fun ScanScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: ScanViewModel = hiltViewModel()
) {
    ScanScreenContent(
        modifier = modifier,
        uiState = viewModel.state.value,
        onNavigationBack = {
            viewModel.onEvent(ScanUiEvent.NavigationBack)
            navController.popBackStack()
        },
        onRetry = { viewModel.onEvent(ScanUiEvent.Retry) },
        onPermissionGranted = { viewModel.onEvent(ScanUiEvent.PermissionGranted) },
        onImageCaptured = { imageFileName ->
            navController.navigateToScanResult(
                imageFileName = imageFileName,
                navOptions = NavOptions
                    .Builder()
                    .setPopUpTo(navController.currentDestination!!.route, true)
                    .build()
            )
        },
    )
}

@Composable
fun ScanScreenContent(
    modifier: Modifier = Modifier,
    uiState: ScanUiState,
    onNavigationBack: () -> Unit = {},
    onRetry: () -> Unit = {},
    onPermissionGranted: () -> Unit = {},
    onImageCaptured: (String) -> Unit,
) {

    TopBarScaffold(
        title = stringResource(id = R.string.scan),
        navigationIcon = AppIcons.ArrowBack,
        onNavigationClick = { onNavigationBack() },
        onBackHandler = { onNavigationBack() }
    ) { padding ->

        LoadingView(
            modifier = modifier.padding(padding),
            isVisible = uiState.isLoading
        )

        PermissionView(
            modifier = modifier,
            isVisible = uiState.isPermissionGranted.not(),
            permission = Manifest.permission.CAMERA,
            rationalMessage = "The app needs CAMERA permission to use camera for scanning receipt",
            onPermissionGranted = { onPermissionGranted() }
        )

        RetryView(
            isVisible = uiState.isRetry,
            retryMessage = uiState.retryMsg,
            icon = AppIcons.Warning,
            onRetry = onRetry
        )

        AutoRetryView(
            isVisible = uiState.isAutoRetry,
            errorMessage = uiState.autoRetryMsg,
            icon = AppIcons.Warning,
        )
    }

    CameraView(
        modifier = modifier,
        isVisible = uiState.isPermissionGranted,
        onCloseClick = onNavigationBack,
        onImageCaptured = onImageCaptured
    )
}

