package com.example.home

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ui.common.test.TestTag
import com.example.ui.common.test.getString
import com.example.ui.common.test.logTree
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.example.ui.common.R.string as commonString

@RunWith(AndroidJUnit4::class)
internal class HomeScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun test_homeScreen_loading_state() {
        with(composeTestRule) {
            setContent {
                HomeScreenContent(
                    navController = rememberNavController(),
                    uiState = HomeUiState.Loading
                )
            }
            onNode(hasTestTag(TestTag.LOADING)).assertIsDisplayed()
        }
    }

    @Test
    fun test_homeScreen_retry_state() {
        with(composeTestRule) {
            setContent {
                HomeScreenContent(
                    navController = rememberNavController(),
                    uiState = HomeUiState.Retry()
                )
            }
            logTree()
            onNodeWithText(getString(commonString.retry)).assertIsDisplayed()
            onNodeWithContentDescription(getString(commonString.warningIconDescription)).assertIsDisplayed()
            onNodeWithText(getString(commonString.defaultErrorHint)).assertIsDisplayed()
        }
    }

    @Test
    fun test_homeScreen_autoRetry_state() {
        with(composeTestRule) {
            setContent {
                HomeScreenContent(
                    navController = rememberNavController(),
                    uiState = HomeUiState.AutoRetry()
                )
            }
            logTree()
            onNodeWithText(getString(commonString.autoRetryHint)).assertIsDisplayed()
            onNodeWithContentDescription(getString(commonString.warningIconDescription)).assertIsDisplayed()
            onNodeWithText(getString(commonString.defaultErrorHint)).assertIsDisplayed()
        }
    }

    @Test
    fun test_homeScreen_data_state() {
        with(composeTestRule) {
            val rates = exchangeRatesStub()
            setContent {
                HomeScreenContent(
                    navController = rememberNavController(),
                    uiState = HomeUiState.Loaded(rates = rates, favoriteRates = rates.subList(0, 3))
                )
            }
            logTree()
            onNodeWithText(getString(commonString.autoRetryHint)).assertDoesNotExist()
            onNode(hasScrollToIndexAction()).assertIsDisplayed()
            onNode(hasScrollAction()).performScrollToIndex(rates.size - 1)
            onAllNodesWithContentDescription(getString(commonString.favoriteIconDescription))
                .assertCountEquals(rates.size)
        }
    }
}
