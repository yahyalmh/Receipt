package com.example.ui.common.component.bar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy

@Composable
fun BottomAppBar(
    modifier: Modifier = Modifier,
    tabs: List<BottomBarTab>,
    currentDestination: NavDestination? = null,
    onNavigateToDestination: (BottomBarTab) -> Unit
) {
    NavigationBar(
        modifier = modifier
            .zIndex(1F)
            .shadow(
                elevation = 5.dp,
                spotColor = MaterialTheme.colorScheme.onSurface
            ),
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        tonalElevation = 2.dp
    ) {
        tabs.forEach { tab ->
            val selected = currentDestination.isInHierarchy(tab)
            NavigationBarItem(
                enabled = true,
                selected = selected,
                onClick = { onNavigateToDestination(tab) },
                icon = {
                    Icon(
                        imageVector = if (selected) {
                            tab.selectedIcon
                        } else {
                            tab.unselectedIcon
                        },
                        contentDescription = tab.contentDescription
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                ),
                label = { Text(tab.title) }
            )
        }
    }
}


private fun NavDestination?.isInHierarchy(destination: BottomBarTab) =
    this?.hierarchy?.any {
        it.route?.contains(destination.route, true) ?: false
    } ?: false


class BottomBarTab(
    val title: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val contentDescription: String? = null
)

@Preview
@Composable
fun BottomBarPreview() {
    BottomAppBar(tabs = bottomBarTabs()) {}
}

@Composable
fun bottomBarTabs() = listOf(
    BottomBarTab(
        title = "Home",
        route = "route",
        selectedIcon = Icons.Default.Home,
        unselectedIcon = Icons.Default.Home,
    ),

    BottomBarTab(
        title = "Favorite",
        route = "route",
        selectedIcon = Icons.Default.Favorite,
        unselectedIcon = Icons.Default.FavoriteBorder,
    ),

    BottomBarTab(
        title = "Setting ",
        route = "route",
        selectedIcon = Icons.Default.Settings,
        unselectedIcon = Icons.Default.Settings,
    )
)