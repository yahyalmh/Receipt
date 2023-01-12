package com.example.ui.common.component.wrapper

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DefaultAnimatedVisibility(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) = AnimatedVisibility(
    modifier = modifier,
    visible = isVisible,
    enter = fadeIn(),
    exit = fadeOut(),
    content = content
)

