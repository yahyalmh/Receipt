package com.example.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.data.common.model.dto.Photo
import com.example.data.common.model.dto.ReceiptModel
import com.example.scan.nav.navigateToScan
import com.example.ui.common.component.BaseLazyColumn
import com.example.ui.common.component.icon.AppIcons
import com.example.ui.common.component.screen.TopBarScaffold
import com.example.ui.common.component.view.AutoRetryView
import com.example.ui.common.component.view.RetryView
import com.example.ui.common.ext.create
import com.example.ui.home.R

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    HomeScreenContent(
        modifier = modifier,
        navController = navController,
        uiState = viewModel.state.value,
        onRetry = { viewModel.onEvent(HomeUiEvent.Retry) },
    )
}

@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    uiState: HomeUiState,
    onRetry: () -> Unit = {},
) {
    TopBarScaffold(
        title = stringResource(id = R.string.home),
        navigationIcon = AppIcons.Menu,
        navigationIconContentDescription = stringResource(id = R.string.menu),
    ) { padding ->

        HomeShimmerView(
            modifier = modifier.padding(padding),
            isVisible = uiState.isLoading
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

        DataView(
            isVisible = uiState.isLoaded,
            modifier = modifier.padding(padding),
            onFabClick = { navController.navigateToScan() },
            receiptModels = uiState.receipts,
        )
    }
}

@Composable
private fun HomeShimmerView(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
) {
    BaseLazyColumn(
        modifier = modifier,
        isVisible = isVisible,
        models = create(count = 10) { { ReceiptShimmerCell() } }
    )
}

@Composable
private fun DataView(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    receiptModels: List<ReceiptModel> = emptyList(),
    favoritesReceipts: List<ReceiptModel> = emptyList(),
    navigateToDetail: (id: String) -> Unit = {},
    onFavoriteClick: (rate: ReceiptModel) -> Unit = {},
    onFabClick: () -> Unit = {},
) {
    val models = receiptModels.map {
        it.toCell(
            favoritesReceipts = favoritesReceipts,
            navigateToDetail = navigateToDetail,
            onFavoriteClick = onFavoriteClick
        )
    }
    Box(modifier = modifier.fillMaxSize()) {
        BaseLazyColumn(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface),
            isVisible = isVisible,
            models = models
        )
        FloatingActionButton(modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(bottom = 16.dp, end = 16.dp),
            shape = MaterialTheme.shapes.extraLarge,
            onClick = { onFabClick() }) {
            Icon(AppIcons.Add, contentDescription = "Add receipt")
        }
    }

}


@Composable
@Preview(showSystemUi = true)
fun HomeShimmerPreview() {
    HomeShimmerView(isVisible = true)
}

@Composable
@Preview(showSystemUi = true)
fun DataPreview() = DataView(
    receiptModels = create(15) { receiptsStub() },
    isVisible = true
)

fun receiptsStub() =
    ReceiptModel(
        languageId = "English",
        text = "Text",
        translate = "Translate",
        photo = Photo(timestamp = "23423234234")
    )


