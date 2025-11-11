package no.uio.ifi.in2000.joakimcm.solcelleapp.ui.screens.favorites


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import no.uio.ifi.in2000.joakimcm.solcelleapp.PresentationFromFavorites
import no.uio.ifi.in2000.joakimcm.solcelleapp.R
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.favorites.Favorite
import no.uio.ifi.in2000.joakimcm.solcelleapp.ui.theme.gradient


@Composable
fun FavoriteScreen(
    onNavigateToPresentation: (PresentationFromFavorites) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val favorites: List<Favorite> by viewModel.favorites.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.favoritesFromDB()
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient) // Use gradient here
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.favorite_screen_title),
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(favorites) { favorite ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF102837), shape = RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = favorite.address,
                                color = Color.White,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            favorite.angle?.let {
                                Text(
                                    text = stringResource(
                                        R.string.favorite_screen_angle,
                                        it.toInt()
                                    ),
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                            favorite.panelSize?.let {
                                Text(
                                    text = stringResource(
                                        R.string.favorite_screen_area,
                                        it.toInt()
                                    ),
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                        }
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val favoriteDetails = PresentationFromFavorites(
                                        angle = favorite.angle ?: 0.0,
                                        areal = favorite.panelSize ?: 0.0,
                                        lon = favorite.lon,
                                        lat = favorite.lat,
                                        address = favorite.address,
                                        direction = favorite.direction
                                    )
                                    onNavigateToPresentation(favoriteDetails)
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFF333333
                                    )
                                ),
                                modifier = Modifier.width(120.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.favorite_screen_show_details),
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            }
                            Button(
                                onClick = {
                                    viewModel.removeFavoriteFromDatabase(favorite.id)
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFF333333
                                    )
                                ),
                                modifier = Modifier.width(120.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.favorite_screen_delete),
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
