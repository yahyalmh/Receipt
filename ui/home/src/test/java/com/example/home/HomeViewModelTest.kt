package com.example.home

import com.example.data.common.model.ExchangeRate
import com.example.ui.common.test.MainDispatcherRule
import com.example.ui.common.test.thenEmitError
import com.example.ui.common.test.thenEmitNothing
import com.example.favorite.FavoriteRatesInteractorImpl
import com.example.home.util.Constant
import com.example.rate.ExchangeRateInteractorImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MockitoExtension::class, MainDispatcherRule::class)
internal class HomeViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    @Mock
    lateinit var exchangeRateInteractor: ExchangeRateInteractorImpl

    @Mock
    lateinit var favoriteRatesInteractor: FavoriteRatesInteractorImpl
    private lateinit var homeViewModel: HomeViewModel

    private lateinit var exchangeRates: List<ExchangeRate>
    private lateinit var favoriteRates: MutableList<ExchangeRate>

    @BeforeEach
    fun setup() {
        exchangeRates = exchangeRatesStub()
        favoriteRates = mutableListOf(exchangeRates.first())
    }


    @Test
    fun `WHEN fetching rates return data THEN ui state is Loaded`() = runTest {
        whenever(exchangeRateInteractor.getLiveRates(Constant.liveRateFetchInterval)).thenReturn(
            flowOf(exchangeRates)
        )
        whenever(favoriteRatesInteractor.getFavoriteRates()).thenReturn(flowOf(favoriteRates))

        homeViewModel = HomeViewModel(exchangeRateInteractor, favoriteRatesInteractor)
        val uiState = homeViewModel.state.value

        Assertions.assertTrue(uiState is HomeUiState.Loaded)
        Assertions.assertEquals(exchangeRates, uiState.rates)
        Assertions.assertEquals(favoriteRates, uiState.favoriteRates)
        Assertions.assertFalse(uiState.isLoading)
    }

    @Test
    fun `WHEN fetching rates return error THEN ui state is AutoRetry`() =
        runTest {
            whenever(favoriteRatesInteractor.getFavoriteRates()).thenReturn(flowOf(favoriteRates))
            whenever(exchangeRateInteractor.getLiveRates(Constant.liveRateFetchInterval))
                .thenReturn(flow {
                    if (currentTime < 3000) {
                        throw IOException()
                    } else {
                        emit(exchangeRates)
                    }
                })

            homeViewModel = HomeViewModel(exchangeRateInteractor, favoriteRatesInteractor)
            Assertions.assertTrue(homeViewModel.state.value is HomeUiState.AutoRetry)
        }

    @Test
    fun `WHEN fetching rates return error THEN ui state is AutoRetry THEN ui state is Loaded`() =
        runTest {
            whenever(favoriteRatesInteractor.getFavoriteRates()).thenReturn(flowOf(favoriteRates))
            whenever(exchangeRateInteractor.getLiveRates(Constant.liveRateFetchInterval))
                .thenReturn(flow {
                    if (currentTime < 3000) {
                        throw IOException()
                    } else {
                        emit(exchangeRates)
                    }
                })

            homeViewModel = HomeViewModel(exchangeRateInteractor, favoriteRatesInteractor)
            Assertions.assertTrue(homeViewModel.state.value is HomeUiState.AutoRetry)

            advanceTimeBy(4000)
            advanceUntilIdle()
            Assertions.assertTrue(homeViewModel.state.value is HomeUiState.Loaded)
        }


    @Test
    fun `WHEN fetching rates return error THEN after while ui state is Retry`() = runTest {
        whenever(exchangeRateInteractor.getLiveRates(Constant.liveRateFetchInterval))
            .thenEmitError(IOException())
        whenever(favoriteRatesInteractor.getFavoriteRates()).thenEmitNothing()

        homeViewModel = HomeViewModel(exchangeRateInteractor, favoriteRatesInteractor)
        Assertions.assertTrue(homeViewModel.state.value is HomeUiState.AutoRetry)

        advanceUntilIdle()
        Assertions.assertTrue(homeViewModel.state.value is HomeUiState.Retry)
    }


    @Test
    fun `GIVEN retry event THEN data load successfully`() = runTest {
        whenever(exchangeRateInteractor.getLiveRates(Constant.liveRateFetchInterval))
            .thenEmitError(IOException())

        homeViewModel = HomeViewModel(exchangeRateInteractor, favoriteRatesInteractor)
        advanceUntilIdle()
        Assertions.assertTrue(homeViewModel.state.value is HomeUiState.Retry)

        whenever(exchangeRateInteractor.getLiveRates(Constant.liveRateFetchInterval)).thenReturn(
            flowOf(exchangeRates)
        )
        whenever(favoriteRatesInteractor.getFavoriteRates()).thenReturn(flowOf(favoriteRates))
        homeViewModel.onEvent(HomeUiEvent.Retry)

        advanceUntilIdle()
        Assertions.assertTrue(homeViewModel.state.value is HomeUiState.Loaded)
    }

    @Test
    fun `GIVEN favorite event THEN item added or removed from favorites`() = runTest {
        whenever(exchangeRateInteractor.getLiveRates(Constant.liveRateFetchInterval)).thenReturn(
            flowOf(exchangeRates)
        )
        whenever(favoriteRatesInteractor.getFavoriteRates()).thenReturn(flowOf(favoriteRates))

        homeViewModel = HomeViewModel(exchangeRateInteractor, favoriteRatesInteractor)
        advanceUntilIdle()
        Assertions.assertTrue(homeViewModel.state.value is HomeUiState.Loaded)

        homeViewModel.onEvent(HomeUiEvent.OnFavorite(exchangeRates.last()))
        advanceUntilIdle()

        verify(favoriteRatesInteractor).addFavorite(exchangeRates.last())
        favoriteRates.add(exchangeRates.last())

        homeViewModel.onEvent(HomeUiEvent.OnFavorite(exchangeRates.last()))
        advanceUntilIdle()

        verify(favoriteRatesInteractor).removeFavorite(exchangeRates.last())
    }
}