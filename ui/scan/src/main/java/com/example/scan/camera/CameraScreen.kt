package com.example.scan.camera

import android.Manifest
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.scan.camera.CameraUiEvent.*
import com.example.ui.common.component.icon.AppIcons
import com.example.ui.common.component.screen.TopBarScaffold
import com.example.ui.common.component.view.AutoRetryView
import com.example.ui.common.component.view.LoadingView
import com.example.ui.common.component.view.PermissionView
import com.example.ui.common.component.view.RetryView
import com.example.ui.scan.R

@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel = hiltViewModel()
) {
    ScanScreenContent(
        modifier = modifier,
        uiState = viewModel.state,
        onNavigationBack = { viewModel.onEvent(NavigationBack) },
        onRetry = { viewModel.onEvent(Retry) },
        onPermissionGranted = { viewModel.onEvent(PermissionGranted) },
        onImageCaptured = { imageAddress -> viewModel.onEvent(ImageCaptured(imageAddress)) },
    )
}

@Composable
fun ScanScreenContent(
    modifier: Modifier = Modifier,
    uiState: CameraUiState,
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

