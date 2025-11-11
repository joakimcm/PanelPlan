package no.uio.ifi.in2000.joakimcm.solcelleapp.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import no.uio.ifi.in2000.joakimcm.solcelleapp.data.favorites.FavoritesDAO
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.favorites.Favorite
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val dao: FavoritesDAO,
) : ViewModel() {
    private val _favorites = MutableStateFlow<List<Favorite>>(emptyList())
    val favorites get() = _favorites.asStateFlow()

    fun favoritesFromDB() {
        viewModelScope.launch {
            _favorites.value = withContext(Dispatchers.IO) {
                dao.getFavorites()
            }
        }
    }

    fun removeFavoriteFromDatabase(id: Int?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // Delete element with DAO
                dao.deleteFavoriteById(id)
                // Retrieve data again
            }
            favoritesFromDB()
        }
    }
}