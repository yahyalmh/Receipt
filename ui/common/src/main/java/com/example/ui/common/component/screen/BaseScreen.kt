package com.example.ui.common.component.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.ui.common.BaseViewModel
import com.example.ui.common.UIEvent
import com.example.ui.common.UIState
import com.example.ui.common.component.bar.TopAppBar
import com.example.ui.common.component.icon.AppIcons
import com.example.ui.common.component.view.AutoRetryView
import com.example.ui.common.component.view.LoadingView
import com.example.ui.common.component.view.RetryView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: BaseViewModel<BaseUiState, UIEvent>,
    screenTitle: String,
    navigationIcon: ImageVector? = AppIcons.ArrowBack,
    navigationIconContentDescription: String? = "Back Icon",
    onNavigationClick: () -> Unit = { navController.popBackStack() },
    actionIcon: ImageVector? = null,
    actionIconContentDescription: String? = null,
    onActionClick: () -> Unit = {},
    onRetry: () -> Unit = {},
    contentView: @Composable () -> Unit = {},
) {
    val uiState = viewModel.state

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = screenTitle,
                modifier = Modifier
                    .zIndex(1F)
                    .shadow(
                        elevation = 5.dp,
                        spotColor = MaterialTheme.colorScheme.onBackground
                    ),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                navigationIcon = navigationIcon,
                onNavigationClick = onNavigationClick,
                navigationIconContentDescription = navigationIconContentDescription,
                actionIcon = actionIcon,
                actionIconContentDescription = actionIconContentDescription,
                onActionClick = onActionClick,
            )
        }
    ) { padding ->

        LoadingView(isVisible = uiState.isLoading)

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

        ContentView(
            modifier = modifier
                .padding(padding)
                .background(MaterialTheme.colorScheme.surface),
            isVisible = uiState.isLoaded,
            content = contentView
        )
    }
}


open class BaseUiState(
    open var isLoading: Boolean = false,
    open val isLoaded: Boolean = false,
    open val isRetry: Boolean = false,
    open val retryMsg: String = "",
    open val isAutoRetry: Boolean = false,
    open val autoRetryMsg: String = "",
) : UIState

@Composable
private fun ContentView(
    modifier: Modifier,
    isVisible: Boolean,
    content: @Composable () -> Unit
) {
    if (isVisible) {
        Column(modifier = modifier) {
            content()
        }
    }
}
