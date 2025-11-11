package no.uio.ifi.in2000.joakimcm.solcelleapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import no.uio.ifi.in2000.joakimcm.solcelleapp.ui.screens.angleInput.AngleInputScreen
import no.uio.ifi.in2000.joakimcm.solcelleapp.ui.screens.favorites.FavoriteScreen
import no.uio.ifi.in2000.joakimcm.solcelleapp.ui.screens.front.FrontScreen
import no.uio.ifi.in2000.joakimcm.solcelleapp.ui.screens.front.GeoDataViewModel
import no.uio.ifi.in2000.joakimcm.solcelleapp.ui.screens.info.InfoScreen
import no.uio.ifi.in2000.joakimcm.solcelleapp.ui.screens.presentation.PresentationScreen
import no.uio.ifi.in2000.joakimcm.solcelleapp.ui.screens.startscreen.StartScreen
import no.uio.ifi.in2000.joakimcm.solcelleapp.ui.theme.gradient


@Composable
fun PanelPlanApp(
    navController: NavHostController = rememberNavController(),
) {
    // Shared viewModel
    val geoDataViewModel: GeoDataViewModel = hiltViewModel()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // These lines find the route to some screens
    // Used to check if back-arrow should be shown
    val isAtStart = currentRoute == Front::class.qualifiedName
    val isAtAnimation = currentRoute == StartScreen::class.qualifiedName

    Scaffold(

        topBar = {
            // To see details about the topBar -> Look at function SolcelleAppBar
            SolCelleAppBar(
                canNavigateBack = !isAtAnimation && !isAtStart, // Can only navigate back if not at start og animation
                navigateBack = { navController.navigateUp() },
                // title = title
            )


        }, bottomBar = {
            BottomNavigationBar(navController)

        }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)  // Keeps default Scaffold padding
                .fillMaxSize()
                .background(gradient)
        ) {
            NavHost(
                navController = navController,
                startDestination = StartScreen,
                modifier = Modifier.fillMaxSize()
            ) {
                /*
    This project uses type-safe navigation.

    The main difference from traditional navigation is that we don't use string-based routes,
    but instead define serializable objects or data classes.

    See "NavigationClasses.kt" for details on these route objects and data classes.

    To pass parameters, a lambda function is used in the Composable that initiates navigation.
    The receiving Composable should create a `val` using `it.toRoute` to extract the passed parameters.

    For an example, see `FrontScreen` and the `onNavigateToHome` parameter,
    and then refer to the <Home> Composable definition in this function.
*/

                composable<StartScreen> {
                    StartScreen(
                        onNavigateToFront = {
                            navController.navigate(Front) {
                                popUpTo("StartScreen") { inclusive = true }
                            }
                        },
                    )
                }

                composable<Front> {
                    FrontScreen(
                        onNavigateToAngleScreen = {
                            navController.navigate(AngleInput)
                        }, viewModel = geoDataViewModel
                    )
                }
                composable<AngleInput> {
                    AngleInputScreen(onNavigateToPresentation = { presentationDetails ->
                        navController.navigate(
                            Presentation(
                                angle = presentationDetails.angle,
                                areal = presentationDetails.areal,
                                direction = presentationDetails.direction
                            )
                        )
                    })
                }
                composable<Info> {
                    InfoScreen()
                }
                composable<Favorite> {
                    FavoriteScreen(onNavigateToPresentation = { favoriteDetails ->
                        navController.navigate(
                            PresentationFromFavorites(
                                angle = favoriteDetails.angle,
                                areal = favoriteDetails.areal,
                                lon = favoriteDetails.lon,
                                lat = favoriteDetails.lat,
                                address = favoriteDetails.address,
                                direction = favoriteDetails.direction
                            )
                        )
                    })
                }
                composable<Presentation> {
                    val presentation = it.toRoute<Presentation>()
                    val selectedAddress by geoDataViewModel.selectedAddress.collectAsState()
                    val address = selectedAddress?.adressetekst
                    selectedAddress?.representasjonspunkt?.let { point ->
                        PresentationScreen(
                            lon = point.lon.toString(),
                            lat = point.lat.toString(),
                            angle = presentation.angle,
                            panelSize = presentation.areal,
                            direction = presentation.direction,
                            address = address,
                            fromFavorite = false
                        )
                    }
                }
                composable<PresentationFromFavorites> {
                    val presentation = it.toRoute<PresentationFromFavorites>()
                    PresentationScreen(
                        lon = presentation.lon,
                        lat = presentation.lat,
                        angle = presentation.angle,
                        panelSize = presentation.areal,
                        address = presentation.address,
                        direction = presentation.direction,
                        fromFavorite = true
                    )
                }
            }
        }
    }
}

// Function that are reused on every screen -> topBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolCelleAppBar(
    canNavigateBack: Boolean,
    navigateBack: () -> Unit,
) {
    //Box(modifier = Modifier.fillMaxSize().background(gradient)) {
    TopAppBar(modifier = Modifier
        .height(70.dp)
        .fillMaxWidth()
        .background(Color(0xFF02719E)),
        colors = TopAppBarDefaults.topAppBarColors(

            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.navigate_back),
                        tint = Color.White
                    )
                }
            }
        },
        title = {})
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.FrontScreen, BottomNavItem.InfoScreen, BottomNavItem.FavoriteScreen
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(modifier = Modifier.background(Color(0xFF000000))) {
        NavigationBar(
            containerColor = Color.Transparent, // To show gradient
        ) {
            fun onBottomNavItemClick(destination: Any) {
                navController.navigate(destination) {
                    launchSingleTop = true
                    restoreState = true
                }
            }

            items.forEach { item ->
                NavigationBarItem(icon = {
                    Icon(
                        item.icon, contentDescription = stringResource(
                            R.string.general_navigate, item.label
                        ), tint = Color.White
                    )
                },
                    label = { Text(item.label, color = Color.White) },
                    selected = currentRoute == item.route.toString(),
                    onClick = {
                        onBottomNavItemClick(item.route)
                    })
            }
        }
    }
}