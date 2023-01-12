package com.example.scan

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.ui.common.component.icon.AppIcons
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asExecutor
import java.util.concurrent.Executor
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import com.example.ui.scan.R

@Composable
fun CameraView(
    modifier: Modifier = Modifier,
    isVisible: Boolean = false,
    onCloseClick: () -> Unit = {},
    onImageCaptured: (String) -> Unit = {},
) {
    val imageCapture = remember { ImageCapture.Builder().build() }
    val context = LocalContext.current

    AnimatedVisibility(
        modifier = modifier,
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            CameraPreview(modifier = modifier.weight(1f), imageCapture = imageCapture)

            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                IconButton(
                    modifier = Modifier.padding(8.dp).align(Alignment.CenterStart),
                    onClick = { onCloseClick() }) {
                    Icon(
                        modifier = Modifier.size(40.dp),
                        imageVector = AppIcons.Close,
                        contentDescription = stringResource(id = R.string.close),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Box(modifier = Modifier
                    .padding(bottom = 10.dp, top = 8.dp )
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface)
                    .padding(5.dp)
                    .drawBehind { drawCircle(color = Color.Gray, style = Stroke(width = 5f)) }
                    .align(Alignment.Center)
                    .clickable {
                        takePhoto(
                            imageCapture = imageCapture,
                            onImageCaptured = onImageCaptured,
                            context = context
                        )
                    })
            }
        }
    }
}

private fun takePhoto(
    imageCapture: ImageCapture,
    onImageCaptured: (String) -> Unit,
    context: Context
) {
    val executor = ContextCompat.getMainExecutor(context)
    val fileName = "${System.currentTimeMillis()}.jpeg"
    val fileOutput = context.openFileOutput(fileName, Context.MODE_PRIVATE)
    val outputFileOption = ImageCapture.OutputFileOptions.Builder(fileOutput).build()

    imageCapture.takePicture(
        outputFileOption,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                onImageCaptured(fileName)
            }

            override fun onError(e: ImageCaptureException) {
                e.printStackTrace()
            }
        }
    )
}

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    imageCapture: ImageCapture,
    scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        modifier = modifier,
        factory = { context ->
            val executor = ContextCompat.getMainExecutor(context)
            val previewView = PreviewView(context).apply {
                this.scaleType = scaleType
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build()
                    .also { it.setSurfaceProvider(previewView.surfaceProvider) }
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, preview, imageCapture
                    )
                } catch (e: Exception) {
                    Log.e("CameraPreview", "Use case binding failed", e)
                }
            }, executor)
            previewView
        }
    )
}

fun CoroutineContext.asExecutors(): Executor =
    (get(ContinuationInterceptor) as CoroutineDispatcher).asExecutor()




