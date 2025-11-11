package no.uio.ifi.in2000.joakimcm.solcelleapp.ui.screens.front

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import com.mapbox.geojson.Point
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import no.uio.ifi.in2000.joakimcm.solcelleapp.R
import no.uio.ifi.in2000.joakimcm.solcelleapp.ui.theme.PanelPlanAppTheme

/*
First screen user meets, they're asked to enter the address they want to check.
Small bug: The "enter" key on the computer you're using will not work, you have to use the "enter" button in the emulator.
Intended use is to press the address that fits yours in the dropdown menu. If you press "enter", the top one is selected.
 */

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun FrontScreen(
    onNavigateToAngleScreen: () -> Unit,
    viewModel: GeoDataViewModel,
) {
    PanelPlanAppTheme {

        // List of address objects from API
        val uiState by viewModel.uiState.collectAsState()

        // Chosen address in UI
        val selectedAddress by viewModel.selectedAddress.collectAsState(null)

        val mapViewportState = rememberMapViewportState {
            setCameraOptions {
                center(Point.fromLngLat(10.75, 63.0)) // Oslo
                zoom(3.2)
                pitch(0.0)
                bearing(0.0)
            }
        }

        LaunchedEffect(selectedAddress) {
            val point = selectedAddress?.representasjonspunkt ?: return@LaunchedEffect
            // Small delay to let Compose settle (e.g., 200ms)
            kotlinx.coroutines.delay(700)

            mapViewportState.flyTo(cameraOptions {
                center(Point.fromLngLat(point.lon, point.lat))
                zoom(18.0)
            }, MapAnimationOptions.mapAnimationOptions {
                duration(2500)
            })
        }

        // Address that will be used
        var addressInput by rememberSaveable { mutableStateOf("") }

        // Logic
        var expanded by remember { mutableStateOf(false) }
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current
        val scrollState = rememberScrollState()

        // Updates address that are chosen to show user correct input
        LaunchedEffect(selectedAddress) {
            selectedAddress?.let {
                if (addressInput != it.adressetekst) {
                    addressInput = it.adressetekst
                }
            }
        }

        // Use debounce to not make more API-calls than required
        LaunchedEffect(Unit) {
            snapshotFlow { addressInput }.debounce(300L) // Wait for 300ms after typing stops
                .distinctUntilChanged().collect { input ->
                    if (input != "") {
                        viewModel.fetchAddressMap(input)
                    }
                }
        }


        Box(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                })
            }) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .verticalScroll(scrollState)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    MapboxMap(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(15.dp),
                        mapViewportState = mapViewportState
                    ) {
                        selectedAddress?.let {
                            val point = Point.fromLngLat(
                                it.representasjonspunkt.lon, it.representasjonspunkt.lat
                            )
                            val marker = rememberIconImage(
                                key = R.drawable.red_marker,
                                painter = painterResource(R.drawable.red_marker)
                            )
                            PointAnnotation(point = point) {
                                iconImage = marker
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))


                ExposedDropdownMenuBox(
                    expanded = expanded, onExpandedChange = {
                        expanded = !expanded
                    }, modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    val containerColor = Color(0xFFDCF0FB) // Same yellowish-orange color
                    TextField(
                        value = addressInput,
                        onValueChange = { newInput ->
                            addressInput = newInput
                            expanded = true // Show dropdown menu when input changes
                        },
                        label = { Text(stringResource(R.string.front_screen_adress_input)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.front_screen_search)
                            )
                        },
                        trailingIcon = {
                            if (addressInput.isNotEmpty()) {
                                Icon(imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.front_screen_clear),
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .clickable {
                                            addressInput = ""
                                        })
                            }
                        },
                        // Control the "enter" button
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                // Try to select first result if "enter" is pressed on emulator
                                val first =
                                    (uiState as? AddressUiState.Success)?.addresses?.firstOrNull()
                                if (first != null) {
                                    viewModel.setSelectedAddress(first)
                                    expanded = false
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                }
                            }
                        ),
                        modifier = Modifier
                            .menuAnchor()
                            .zIndex(1f)
                            .fillMaxWidth(0.8f)
                            .height(56.dp)
                            .align(Alignment.CenterHorizontally),
                        shape = RoundedCornerShape(30.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = containerColor,
                            unfocusedContainerColor = containerColor,
                            disabledContainerColor = containerColor
                        )

                    )
                    if (addressInput != "") { // Make sure dropdown doesn't show before typing
                        DropdownMenu(
                            expanded = expanded, onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .background(Color(0xFFDCF0FB)),
                            properties = PopupProperties(
                                focusable = false
                            ),
                        ) {
                            when (uiState) {
                                is AddressUiState.Success -> {
                                    val addresses = (uiState as AddressUiState.Success).addresses
                                    // Display address options fetched from the API
                                    addresses.forEachIndexed { index, address ->
                                        DropdownMenuItem(onClick = {
                                            viewModel.setSelectedAddress(address) // Save user's selection in ViewModel
                                            expanded = false
                                            keyboardController?.hide() // Hide the keyboard after selection
                                        }, text = {
                                            Text(text = "${address.adressetekst}\n${address.postnummer} ${address.poststed}")

                                        }, modifier = Modifier.fillMaxWidth()

                                        )
                                        // Only show divider *between* items
                                        if (index < addresses.lastIndex) {
                                            HorizontalDivider()
                                        }
                                    }
                                }

                                is AddressUiState.Error -> {
                                    val melding = (uiState as AddressUiState.Error).message
                                    DropdownMenuItem(
                                        onClick = { /* no-op */ },
                                        enabled = false,
                                        text = {
                                            Text(
                                                melding,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp)
                                            )

                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(230.dp))

                // Button to confirm address selection and navigate to home
                selectedAddress?.let {
                    Button(
                        onClick = {
                            keyboardController?.hide()
                            onNavigateToAngleScreen()
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .height(56.dp),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text(
                            (stringResource(R.string.front_screen_use_adress)), color = Color.White
                        )
                    }
                }
            }
        }
    }
}

