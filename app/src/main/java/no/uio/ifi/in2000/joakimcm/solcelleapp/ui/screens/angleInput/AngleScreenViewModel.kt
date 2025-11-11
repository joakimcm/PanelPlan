package no.uio.ifi.in2000.joakimcm.solcelleapp.ui.screens.angleInput

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import no.uio.ifi.in2000.joakimcm.solcelleapp.domain.kalkulasjoner.roofAngle
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.angle.AngleScreenState
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.angle.Direction

sealed class AngleUiState {

    data class Success(val state: AngleScreenState) : AngleUiState()

    data class Error(val message: String) : AngleUiState()

    data object Loading : AngleUiState()

    data object Idle : AngleUiState()

}

class AngleScreenViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<AngleUiState>(AngleUiState.Idle)
    val uiState: StateFlow<AngleUiState> = _uiState.asStateFlow()


    private val _angleState = MutableStateFlow(
        AngleScreenState(
            height = "", width = "", panelSize = "", angle = null, direction = Direction.SOUTH
        )
    )
    val angleState get() = _angleState.asStateFlow()
    
    fun setHeight(h: String) {
        _angleState.update { currentState ->
            currentState.copy(
                height = h, angle = roofAngle(
                    h.toDoubleOrNull() ?: 0.0, currentState.width.toDoubleOrNull() ?: 0.0
                )
            )
        }
    }


    fun setWidth(b: String) {
        _angleState.update { currentState ->
            currentState.copy(
                width = b, angle = roofAngle(
                    currentState.height.toDoubleOrNull() ?: 0.0, b.toDoubleOrNull() ?: 0.0
                )
            )
        }
    }

    fun setPanelSize(a: String) {
        _angleState.update { currentState -> currentState.copy(panelSize = a) }
    }

    fun setDirection(r: Direction) {
        _angleState.update { currentState ->
            currentState.copy(
                direction = r
            )
        }
    }
}
