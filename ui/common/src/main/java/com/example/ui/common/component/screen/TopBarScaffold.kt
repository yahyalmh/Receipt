package com.example.ui.common.component.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.ui.common.component.bar.TopAppBar
import java.lang.reflect.InvocationHandler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarScaffold(
    title: String,
    navigationIcon: ImageVector? = null,
    navigationIconContentDescription: String? = null,
    onNavigationClick: () -> Unit = {},
    actionIcon: ImageVector? = null,
    actionIconContentDescription: String? = null,
    actionIconColor: Color = MaterialTheme.colorScheme.onSurface,
    onActionClick: () -> Unit = {},
    onBackHandler: () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    BackHandler { onBackHandler() }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = title,
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
                actionIconColor = actionIconColor,
                onActionClick = onActionClick,
            )
        },
        content = content
    )
}