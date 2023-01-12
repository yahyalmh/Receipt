package com.example.ui.common.component.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.ui.common.component.bar.SearchBar

@Composable
fun SearchBarScaffold(
    modifier: Modifier = Modifier,
    hint: String,
    onQueryChange: (query: String) -> Unit,
    onCancelClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxSize()
    ) {
        Column(modifier = modifier.background(MaterialTheme.colorScheme.surface)) {

            SearchBar(
                modifier = modifier.background(MaterialTheme.colorScheme.surface),
                hint = hint,
                onQueryChange = onQueryChange,
                onCancelClick = onCancelClick
            )
            content()
        }
    }
}