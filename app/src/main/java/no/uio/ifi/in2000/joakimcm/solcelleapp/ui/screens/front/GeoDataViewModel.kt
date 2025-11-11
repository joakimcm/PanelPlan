package no.uio.ifi.in2000.joakimcm.solcelleapp.ui.screens.front

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import no.uio.ifi.in2000.joakimcm.solcelleapp.data.geonorge.GeoDataRepository
import no.uio.ifi.in2000.joakimcm.solcelleapp.data.util.DispatcherProvider
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.geonorge.Address
import javax.inject.Inject

sealed class AddressUiState {
    data class Success(val addresses: List<Address>) : AddressUiState()
    data class Error(val message: String) : AddressUiState()
}


@HiltViewModel
class GeoDataViewModel @Inject constructor(
    private val repository: GeoDataRepository,
    private val dispatchers: DispatcherProvider,
) : ViewModel() {

    // uiState will be a list of address objects
    private val _uiState = MutableStateFlow<AddressUiState>(AddressUiState.Success(emptyList()))
    val uiState: StateFlow<AddressUiState> = _uiState.asStateFlow()

    // To remember chosen address from user
    private val _selectedAddress = MutableStateFlow<Address?>(null)
    val selectedAddress: StateFlow<Address?> = _selectedAddress.asStateFlow()

    fun fetchAddressMap(addressInput: String) {
        viewModelScope.launch {
            try {
                val response = withContext(dispatchers.io) {
                    repository.getCoordinates(addressInput)
                }
                _uiState.value = AddressUiState.Success(response)
            } catch (e: Exception) {
                _uiState.value = AddressUiState.Error("${e.message}")
            }
        }
    }

    fun setSelectedAddress(addressChoice: Address?) {
        _selectedAddress.value = addressChoice
    }
}