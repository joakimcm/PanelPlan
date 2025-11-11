package no.uio.ifi.in2000.joakimcm.solcelleapp

import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.joakimcm.solcelleapp.data.geonorge.GeoDataRepository
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.geonorge.Address
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.geonorge.Representasjonspunkt
import no.uio.ifi.in2000.joakimcm.solcelleapp.ui.screens.front.AddressUiState
import no.uio.ifi.in2000.joakimcm.solcelleapp.ui.screens.front.GeoDataViewModel
import no.uio.ifi.in2000.joakimcm.solcelleapp.utilFiler.MainDispatcherRule
import no.uio.ifi.in2000.joakimcm.solcelleapp.utilFiler.TestDispatcherProvider
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class GeoDataViewModelTest {

    // Arrange dispatchers
    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    private val fakeRepo = mockk<GeoDataRepository>()
    private val dispatcherProvider = TestDispatcherProvider(testDispatcher)
    private lateinit var viewModel: GeoDataViewModel

    @Test
    fun fetchAddressMap_to_success() = runTest {
        // Arrange
        val expected = listOf(
            Address("Test Address", "1234", "Oslo", Representasjonspunkt("EPSG:4258", 59.91, 10.75))
        )

        coEvery { fakeRepo.getCoordinates("Oslo") } returns expected

        viewModel = GeoDataViewModel(repository = fakeRepo, dispatchers = dispatcherProvider)

        // Act
        viewModel.fetchAddressMap("Oslo")
        advanceUntilIdle() // Wait for coroutine to be done

        // Assert
        val state = viewModel.uiState.value
        assert(state is AddressUiState.Success)
        assertEquals(expected, (state as AddressUiState.Success).addresses)
    }

    @Test
    fun fetchAddressMap_to_error() = runTest {
        // Arrange
        coEvery { fakeRepo.getCoordinates("ErrorByen") } throws Exception("API Failure")

        viewModel = GeoDataViewModel(repository = fakeRepo, dispatchers = dispatcherProvider)

        // Act
        viewModel.fetchAddressMap("ErrorByen")
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assert(state is AddressUiState.Error)
    }
}