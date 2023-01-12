package com.example.ui.common.component.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.common.R
import com.example.ui.common.component.icon.AppIcons
import com.google.accompanist.permissions.*
import kotlinx.coroutines.*


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionView(
    modifier: Modifier = Modifier,
    isVisible: Boolean = false,
    permission: String,
    rationalMessage: String,
    onPermissionGranted: () -> Unit,
) {
    if (isVisible) {
        var isPermissionResultReceived by remember { mutableStateOf(false) }
        val permissionState = rememberPermissionState(permission) {
            isPermissionResultReceived = true
        }
        if (permissionState.status.isGranted.not()) {
            LaunchedEffect(key1 = "key") { permissionState.launchPermissionRequest() }
        } else {
            onPermissionGranted()
        }

        val screenConfig = getScreenConfig(
            permissionState,
            rationalMessage,
            LocalContext.current
        )
        if (screenConfig != null && isPermissionResultReceived) {
            Column(
                modifier = modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(10.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(
                    space = 30.dp,
                    alignment = Alignment.CenterVertically
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = modifier
                        .padding(12.dp)
                        .size(120.dp),
                    imageVector = screenConfig.icon,
                    contentDescription = stringResource(id = R.string.warningIconDescription),
                    tint = MaterialTheme.colorScheme.error
                )

                Text(
                    text = screenConfig.message,
                    modifier = modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )

                screenConfig.buttonText?.let {
                    OutlinedButton(
                        onClick = { screenConfig.action() },
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            modifier = Modifier.padding(start = 12.dp, end = 12.dp),
                            text = it,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
        }
    }
}

class ScreenConfig(
    val icon: ImageVector,
    val message: String,
    val buttonText: String?,
    val action: () -> Unit
)

@OptIn(ExperimentalPermissionsApi::class)
private fun getScreenConfig(
    permissionState: PermissionState,
    rationalMessage: String,
    context: Context,
) =
    if (permissionState.status.isGranted.not()) {
        if (permissionState.status.shouldShowRationale) {
            ScreenConfig(
                AppIcons.Warning, rationalMessage, "Grant Access"
            ) { permissionState.launchPermissionRequest() }
        } else {
            ScreenConfig(
                AppIcons.Settings,
                "You denied permission twice, please go to settings to grant access",
                "Settings"
            ) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri =
                    Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            }
        }
    } else {
        null
    }