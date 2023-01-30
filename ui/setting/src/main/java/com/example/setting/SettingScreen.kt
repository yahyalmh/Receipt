package com.example.setting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ui.common.ThemeType
import com.example.ui.common.component.screen.TopBarScaffold
import com.example.ui.common.component.view.*

@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = hiltViewModel()
) {
    SettingScreenContent(
        modifier = modifier,
        uiState = viewModel.state,
        onChangeTheme = { viewModel.onEvent(SettingUiEvent.ChangeTheme(it)) }
    )
}

@Composable
private fun SettingScreenContent(
    modifier: Modifier = Modifier,
    uiState: SettingUiState,
    onChangeTheme: (theme: ThemeType) -> Unit
) {
    TopBarScaffold(
        title = stringResource(id = R.string.setting),
    ) { padding ->

        LoadingView(isVisible = uiState.isLoading)

        ContentView(
            modifier = modifier.padding(padding),
            isVisible = uiState.isLoaded,
            currentThemeType = uiState.currentThemeType, onChangeTheme = onChangeTheme
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ContentView(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    currentThemeType: ThemeType?,
    onChangeTheme: (theme: ThemeType) -> Unit
) {
    val themeTypes = ThemeType.values().asList()

    AnimatedVisibility(modifier = modifier, visible = isVisible) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                color = MaterialTheme.colorScheme.onBackground,
                text = stringResource(id = R.string.theme),
                style = MaterialTheme.typography.titleLarge
            )

            Row {
                themeTypes.forEach { type ->
                    AssistChip(
                        modifier = Modifier.padding(8.dp),
                        onClick = { onChangeTheme(type) },
                        label = { Text(type.name) },
                        leadingIcon = {
                            AnimatedVisibility(visible = currentThemeType == type) {
                                Icon(
                                    modifier = Modifier.size(AssistChipDefaults.IconSize),
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = stringResource(id = R.string.checkIconDescription)
                                )
                            }
                        }
                    )
                }
            }

            Divider(modifier = Modifier.padding(top = 6.dp))
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun ContentPreview() {
    SettingScreenContent(uiState = SettingUiState.SetSetting(ThemeType.SYSTEM), onChangeTheme = {})
}