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
import no.uio.ifi.in2000.joakimcm.solcelleapp.data.strom.PowerRepository
import no.uio.ifi.in2000.joakimcm.solcelleapp.data.util.DispatcherProvider
import java.time.LocalDate
import javax.inject.Inject

sealed class PowerUiState {
    data class Success(val monthlyAverage: Map<Int, Double>) : PowerUiState()
    data class Error(val message: String) : PowerUiState()
    data object Loading : PowerUiState()
    data object Idle : PowerUiState()
}

@HiltViewModel
class PowerViewModel @Inject constructor(
    private val powerRepository: PowerRepository,
    private val dispatchers: DispatcherProvider,
) : ViewModel() {

    private var hasLoaded = false

    private val _uiState = MutableStateFlow<PowerUiState>(PowerUiState.Idle)
    val uiState: StateFlow<PowerUiState> = _uiState.asStateFlow()

    fun getMonthlyAverage2024() {
        if (hasLoaded) return
        hasLoaded = true
        viewModelScope.launch {
            _uiState.value = PowerUiState.Loading
            try {
                val lastYear = LocalDate.now().minusYears(1)

                val result = withContext(dispatchers.io) {
                    powerRepository.getMonthlyAverage2024(lastYear)
                }

                _uiState.value = PowerUiState.Success(result)
            } catch (e: Exception) {
                _uiState.value = PowerUiState.Error("${e.message}")
                Log.e("loadMonthlyEnergy", "Error: ${e.message}")
            }
        }
    }


}