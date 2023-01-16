package com.example.scan

import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.graphics.Typeface
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.graphics.toRectF
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.scan.result.MLResult
import com.example.scan.result.ScanResultUiEvent.*
import com.example.scan.result.ScanResultUiState
import com.example.scan.result.ScanResultViewModel
import com.example.ui.common.component.icon.AppIcons
import com.example.ui.common.component.screen.TopBarScaffold
import com.example.ui.common.component.view.ThreeCircleLoadingView
import com.example.ui.common.component.view.TwoOptionAlertDialog
import com.example.ui.scan.R
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer

@Composable
fun ScanResultScreen(
    modifier: Modifier = Modifier,
    viewModel: ScanResultViewModel = hiltViewModel()
) {
    val uiState = viewModel.state.value
    val context = LocalContext.current

    ScanResultScreenContent(
        modifier = modifier,
        uiState = uiState,
        onNavigationBack = { viewModel.onEvent(NavigationBack) },
        onSaveClick = {
            viewModel.onEvent(SaveReceipt(onImageSaved = {
                viewModel.onEvent(NavigationBack)
                showToast(context, "Image saved successfully")
            }))
        },
        onRetakeClick = { viewModel.onEvent(Retake) },
        onDiscardListener = { viewModel.onEvent(DiscardReceipt) },
        onDismissListener = { viewModel.onEvent(DismissDialog) },
    )
}

@Composable
fun ScanResultScreenContent(
    modifier: Modifier = Modifier,
    uiState: ScanResultUiState,
    onNavigationBack: () -> Unit,
    onSaveClick: () -> Unit,
    onRetakeClick: () -> Unit,
    onDiscardListener: () -> Unit,
    onDismissListener: () -> Unit,
) {
    TopBarScaffold(title = stringResource(id = R.string.scan),
        navigationIcon = AppIcons.ArrowBack,
        onNavigationClick = { onNavigationBack() },
        onBackHandler = { onNavigationBack() }) { padding ->

        ThreeCircleLoadingView(
            modifier = modifier,
            isVisible = uiState.isLoading,
            message = uiState.loadingMessage,
        )

        TwoOptionAlertDialog(
            isVisible = uiState.isDiscardingReceipt,
            title = "Discard Receipt?",
            message = "If you go back, you will lose scanned receipt",
            confirmColor = Color.Red,
            confirmText = "Discard",
            dismissText = "Cancel",
            onConfirmListener = onDiscardListener,
            onDismissListener = onDismissListener
        )

        DataView(
            modifier = modifier.padding(padding),
            imagePath = uiState.imageUri,
            mlResult = uiState.mlResult,
            isVisible = uiState.isMlResultReturned || uiState.isImageLoaded,
            showTwoButton = uiState.isMlResultReturned,
            onRetakeClick = onRetakeClick,
            onSaveClick = onSaveClick
        )
    }
}

fun showToast(context: Context, toastMsg: String?) =
    Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()

@Composable
private fun DataView(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    imagePath: String?,
    mlResult: MLResult?,
    showTwoButton: Boolean,
    onRetakeClick: () -> Unit,
    onSaveClick: () -> Unit,
) {
    if (isVisible) {
        var isImageLoading by remember { mutableStateOf(true) }

        Column {
            Column(
                modifier = modifier
                    .verticalScroll(rememberScrollState())
                    .weight(1f)
            ) {
                AsyncImage(modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .fillMaxWidth()
                    .placeholder(
                        visible = isImageLoading,
                        color = Color.LightGray,
                        highlight = PlaceholderHighlight.shimmer(Color.DarkGray),
                        shape = RoundedCornerShape(15),
                    )
                    .aspectRatio(16f / 9f),
                    model = imagePath,
                    contentScale = ContentScale.FillBounds,
                    contentDescription = "receipt image",
                    onSuccess = { isImageLoading = false })
                mlResult?.let {
                    TextWithDescription(title = "Language ID", description = it.languageId)
                    TextWithDescription(title = "Scanned Text", description = it.text.text)
                    TextWithDescription(title = "Translated Text", description = it.translate)
                }
            }

            TwoButton(isVisible = showTwoButton,
                firstButtonText = "Retake",
                secondButtonText = "Save",
                firstButtonOnclick = { onRetakeClick() },
                secondButtonOnclick = { onSaveClick() })
        }

        DrawText(mlResult = mlResult)
    }
}

@Composable
fun DrawText(modifier: Modifier = Modifier, mlResult: MLResult?) {
    val textPaint = Paint().asFrameworkPaint().apply {
        with(LocalDensity.current) {
            isAntiAlias = true
            textSize = 10.sp.toPx()
            color = android.graphics.Color.BLUE
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        }
    }

    val de = LocalDensity.current

    Canvas(
        modifier = modifier
            .zIndex(2f)
            .fillMaxSize()
            .background(Color.Transparent, RoundedCornerShape(10.dp)),
        onDraw = {
            drawIntoCanvas {
                mlResult?.let { result ->
                    result.text.textBlocks.forEach { t ->
                        t.lines.forEach { l ->
                            l.boundingBox?.toRectF()?.let { box ->
                                it.nativeCanvas.drawText(
                                    result.text.toString(),
                                    box.left.div(de.density), // 120.dp.toPx(),            // x-coordinates of the origin (top left)
                                    box.top.div(de.density), // 120.dp.toPx(), // y-coordinates of the origin (top left)
                                    textPaint
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun TextWithDescription(title: String, description: String) {
    Card(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.background), shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 14.dp, bottom = 5.dp),
            style = MaterialTheme.typography.titleSmall,
            text = title
        )

        Divider(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, top = 8.dp, end = 12.dp, bottom = 8.dp),
            style = MaterialTheme.typography.bodyLarge,
            text = description
        )
    }
}

@Composable
private fun TwoButton(
    isVisible: Boolean,
    firstButtonText: String,
    secondButtonText: String,
    firstButtonOnclick: () -> Unit,
    secondButtonOnclick: () -> Unit,
) {
    if (isVisible) {
        ElevatedCard(
            modifier = Modifier
                .zIndex(1f)
                .height(IntrinsicSize.Max)
                .wrapContentWidth(),
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedButton(modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                    onClick = { firstButtonOnclick() }) {
                    Text(text = firstButtonText)
                }

                Spacer(modifier = Modifier.width(2.dp))

                OutlinedButton(modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                    onClick = { secondButtonOnclick() }) {
                    Text(text = secondButtonText)
                }
            }
        }
    }
}

@Composable
@Preview(showSystemUi = false, showBackground = true)
fun TwoButtonPreview() {
    TwoButton(isVisible = true,
        firstButtonText = "Discard",
        secondButtonText = "Cancel",
        firstButtonOnclick = {},
        secondButtonOnclick = {})
}

@Composable
@Preview(uiMode = UI_MODE_NIGHT_MASK, showSystemUi = true)
fun TextWithDescriptionPreview() {
    TextWithDescription(title = "Title", description = "This is the description of the the text")
}
