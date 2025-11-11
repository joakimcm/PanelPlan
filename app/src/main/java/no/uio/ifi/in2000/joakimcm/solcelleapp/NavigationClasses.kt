package no.uio.ifi.in2000.joakimcm.solcelleapp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.angle.Direction

// This is where you include the screens in the application

// First screen
@Serializable
data object StartScreen

@Serializable
data object Front

@Serializable
data object Favorite

@Serializable
data object Info

@Serializable
data object AngleInput

@Serializable
data class Presentation(val angle: Double?, val areal: Double?, val direction: Direction)

@Serializable
data class PresentationFromFavorites(
    val angle: Double?,
    val areal: Double?,
    val lon: String,
    val lat: String,
    val address: String,
    val direction: Direction,
)

// Class for bottom navigation bar
sealed class BottomNavItem(
    val route: Any,
    val icon: ImageVector,
    val label: String,
) {
    data object FrontScreen : BottomNavItem(Front, Icons.Filled.Home, "Hjem")
    data object InfoScreen : BottomNavItem(Info, Icons.Filled.Info, "Info")
    data object FavoriteScreen : BottomNavItem(Favorite, Icons.Filled.Favorite, "Favoritter")
}



