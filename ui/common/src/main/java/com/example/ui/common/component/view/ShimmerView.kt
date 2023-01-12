package com.example.ui.common.component.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.ui.common.test.TestTag


@Composable
fun ShimmerView(
    modifier: Modifier = Modifier,
    duration: Int = 1300,
    interval: Int = 300,
    isVisible: Boolean = false,
    content: @Composable (shimmerAxis: ShimmerAxis) -> Unit,
) {
    val padding = 12.dp
    AnimatedVisibility(
        modifier = modifier.testTag(TestTag.SHIMMER_VIEW),
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val screenWidthPx = with(LocalDensity.current) { (maxWidth - (padding * 2)).toPx() }
            val screenHeightPx = with(LocalDensity.current) { (maxHeight - padding).toPx() }
            val gradientWidth: Float = (0.12f * screenHeightPx)

            val infiniteTransition = rememberInfiniteTransition()
            val xAnimateValue = infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = (screenWidthPx + gradientWidth),
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = duration,
                        easing = LinearEasing,
                        delayMillis = interval
                    ),
                    repeatMode = RepeatMode.Restart
                )
            )
            val yAnimateValue = infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = (screenHeightPx + gradientWidth),
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 1300,
                        easing = LinearEasing,
                        delayMillis = 300
                    ),
                    repeatMode = RepeatMode.Restart
                )
            )

            val endX = xAnimateValue.value
            val startX = xAnimateValue.value - gradientWidth
            val endY = yAnimateValue.value
            val startY = yAnimateValue.value - gradientWidth
            content(ShimmerAxis(startX, endX, startY, endY))
        }
    }
}

fun Modifier.shimmerBackground(
    gradient: ShimmerGradient.Linear,
    shape: Shape = RectangleShape
): Modifier =
    this
        .fillMaxSize()
        .zIndex(1f)
        .background(brush = gradient(), shape = shape)

sealed class ShimmerGradient {
    protected fun createColors(color: Color): List<Color> = listOf(
        color.copy(alpha = .9f),
        color.copy(alpha = .3f),
        color.copy(alpha = .9f),
    )

    class Linear(val color: Color, private val shimmerAxis: ShimmerAxis) :
        ShimmerGradient() {
        operator fun invoke() = Brush.linearGradient(
            createColors(color),
            start = Offset(shimmerAxis.startX, shimmerAxis.startY),
            end = Offset(shimmerAxis.endX, shimmerAxis.endY)
        )
    }

    class Horizontal(val color: Color, private val shimmerAxis: ShimmerAxis) :
        ShimmerGradient() {
        operator fun invoke() = Brush.horizontalGradient(
            createColors(color),
            startX = shimmerAxis.startX,
            endX = shimmerAxis.endX
        )
    }

    class Vertical(private val color: Color, private val shimmerAxis: ShimmerAxis) :
        ShimmerGradient() {
        operator fun invoke() = Brush.verticalGradient(
            createColors(color),
            startY = shimmerAxis.startY,
            endY = shimmerAxis.endY
        )
    }
}

data class ShimmerAxis(
    val startX: Float,
    val endX: Float,
    val startY: Float,
    val endY: Float,
)
