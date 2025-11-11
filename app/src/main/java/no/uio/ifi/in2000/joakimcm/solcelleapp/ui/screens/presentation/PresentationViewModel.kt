package no.uio.ifi.in2000.joakimcm.solcelleapp.ui.screens.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import no.uio.ifi.in2000.joakimcm.solcelleapp.data.favorites.FavoritesDAO
import no.uio.ifi.in2000.joakimcm.solcelleapp.data.util.DispatcherProvider
import no.uio.ifi.in2000.joakimcm.solcelleapp.domain.kalkulasjoner.CalculationService
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.angle.Direction
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.favorites.Favorite
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.frost.MonthlyWeatherData
import javax.inject.Inject

sealed class SunEnergyUiState {
    data class Success(val monthlyEnergyEstimate: Map<Int, Double>) : SunEnergyUiState()
    data class Error(val message: String) : SunEnergyUiState()
    data object Loading : SunEnergyUiState()
    data object Idle : SunEnergyUiState()
}

@HiltViewModel
class PresentationViewModel @Inject constructor(
    private val dao: FavoritesDAO,
    private val calculationService: CalculationService,
    private val dispatchers: DispatcherProvider,
) : ViewModel() {

    private var hasLoaded = false

    private val _uiState = MutableStateFlow<SunEnergyUiState>(SunEnergyUiState.Idle)
    val uiState: StateFlow<SunEnergyUiState> = _uiState.asStateFlow()

    private val _observations = MutableStateFlow<Map<Int, MonthlyWeatherData>>(emptyMap())
    val observations: StateFlow<Map<Int, MonthlyWeatherData>> = _observations.asStateFlow()

    private val _radiation = MutableStateFlow<Map<Int, Double>>(emptyMap())
    val radiation: StateFlow<Map<Int, Double>> = _radiation.asStateFlow()

    fun loadMonthlyEnergy(
        lon: String,
        lat: String,
        angle: Double?,
        size: Double,
        direction: Direction,
    ) {
        if (hasLoaded) return
        hasLoaded = true
        viewModelScope.launch {
            _uiState.value = SunEnergyUiState.Loading
            try {
                val result = withContext(dispatchers.io) {
                    calculationService.getMonthlyEnergyEstimate(
                        lon,
                        lat,
                        angle,
                        size,
                        direction
                    )
                }
                _uiState.value = SunEnergyUiState.Success(result)

            } catch (e: Exception) {
                _uiState.value = SunEnergyUiState.Error("${e.message}")
                Log.e("loadMonthlyEnergy", "Error: ${e.message}")
            }
        }
    }

    fun loadMonthlyObservations() {
        _observations.value = calculationService.getFrostDataFromRepo()
    }

    fun loadMonthlyRadiation() {
        _radiation.value = calculationService.getPvgisDataFromRepo()
    }

    fun addFavorite(favorite: Favorite) {
        viewModelScope.launch {
            withContext(dispatchers.io) {
                dao.insert(favorite)
            }
        }
    }
}