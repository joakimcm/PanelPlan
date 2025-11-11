package no.uio.ifi.in2000.joakimcm.solcelleapp.ui.screens.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.CarRepair
import androidx.compose.material.icons.filled.Dining
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.joakimcm.solcelleapp.R
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.angle.Direction
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.favorites.Favorite
import no.uio.ifi.in2000.joakimcm.solcelleapp.ui.theme.gradient
import no.uio.ifi.in2000.joakimcm.solcelleapp.ui.theme.gradient_button


@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresentationScreen(
    lon: String,
    lat: String,
    angle: Double?,
    panelSize: Double?,
    direction: Direction,
    address: String?,
    viewModel: PresentationViewModel = hiltViewModel(),
    powerViewModel: PowerViewModel = hiltViewModel(),
    fromFavorite: Boolean,
) {
    var addedToFavorite by rememberSaveable { mutableStateOf(fromFavorite) }

    var selectedMonthIndex by rememberSaveable { mutableIntStateOf(5) }
    var expanded by rememberSaveable { mutableStateOf(false) }

    // Success = Map<Int, Double> Der Int er måneden fra 1-12
    val uiState by viewModel.uiState.collectAsState()
    val observations by viewModel.observations.collectAsState()
    val radiation by viewModel.radiation.collectAsState()

    val favoriteSaved = stringResource(R.string.presentation_screen_saved_favorite)

    // Fetches weather data + data from pvgis
    LaunchedEffect(true) {
        if (panelSize != null) {
            viewModel.loadMonthlyEnergy(lon, lat, angle, panelSize, direction)
        }
    }

    val powerUiState by powerViewModel.uiState.collectAsState()

    // Fetch power cost data
    LaunchedEffect(Unit) {
        powerViewModel.getMonthlyAverage2024()
    }

    val monthNames = listOf(
        stringResource(R.string.month_jan),
        stringResource(R.string.month_feb),
        stringResource(R.string.month_mar),
        stringResource(R.string.month_apr),
        stringResource(R.string.month_may),
        stringResource(R.string.month_jun),
        stringResource(R.string.month_jul),
        stringResource(R.string.month_aug),
        stringResource(R.string.month_sep),
        stringResource(R.string.month_oct),
        stringResource(R.string.month_nov),
        stringResource(R.string.month_dec)
    )

    if (uiState is SunEnergyUiState.Success && powerUiState is PowerUiState.Success) {
        // Map<Int, Double>
        val monthlySunEnergy = (uiState as SunEnergyUiState.Success).monthlyEnergyEstimate
        val powerPriceMap = (powerUiState as PowerUiState.Success).monthlyAverage

        // Observations
        viewModel.loadMonthlyObservations()

        // Radiation
        viewModel.loadMonthlyRadiation()

        // Makes a list over sun-power production for every month (1-12) based on weather data and user input
        val powerProductionList = (1..12).map { month ->
            monthlySunEnergy[month] ?: 0.0
        }

        val selectedMonth = selectedMonthIndex + 1

        // Get weather data from the given month
        val temp: Double? = observations[selectedMonth]?.temperature
        val snow: Double? = observations[selectedMonth]?.snowDepth
        val cloud: Double? = observations[selectedMonth]?.cloudCoverage

        val avgRad = radiation[selectedMonth] ?: 0.0
        val producedPower = powerProductionList[selectedMonthIndex]


        val monthlySavings = (1..12).map { month ->
            val productionThisMonth = monthlySunEnergy[month] ?: 0.0
            val powerPriceThisMonth = powerPriceMap[month] ?: 0.60
            productionThisMonth * powerPriceThisMonth
        }

        val powerCostForChosenMonth = powerPriceMap[selectedMonth] ?: 0.60
        val chosenMonthSavings = monthlySavings[selectedMonth]

        val yearlyProduction = powerProductionList.sum()
        val yearlySavings = monthlySavings.sum() //


        LazyColumn(
            modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(gradient)
                        .padding(16.dp)
                ) {
                    val coroutineScope = rememberCoroutineScope()
                    val snackbarHostState = remember { SnackbarHostState() }



                    Text(
                        text = (stringResource(R.string.forventet_produksjon_og_besparelse)),
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 16.dp)
                    )
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.Top
                        ) {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        viewModel.addFavorite(
                                            Favorite(
                                                address = address ?: "",
                                                lon = lon,
                                                lat = lat,
                                                angle = angle,
                                                panelSize = panelSize,
                                                direction = direction
                                            )
                                        )
                                        addedToFavorite = true
                                        snackbarHostState.showSnackbar(favoriteSaved)
                                    }
                                }, enabled = !addedToFavorite
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = stringResource(R.string.presentation_screen_content_description_add_favorite),
                                    tint = if (addedToFavorite) Color.Gray else Color.Red //color of favorite button
                                )
                            }
                        }

                        // Snackbar
                        SnackbarHost(
                            hostState = snackbarHostState,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Energy production box per year
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color(0xFF052B40), shape = RoundedCornerShape(12.dp))
                                .padding(12.dp)
                                .heightIn(min = 72.dp) // <-- Absolute minimum height
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Bolt,
                                    contentDescription = stringResource(R.string.presentation_screen_content_description_production),
                                    tint = Color(0xFFFFA500),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = stringResource(R.string.presentation_screen_produce),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = stringResource(
                                            R.string.presentation_screen_annual_production_text,
                                            yearlyProduction
                                        ), color = Color.White
                                    )
                                }
                            }
                        }

                        // Savings-box
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color(0xFF052B40), shape = RoundedCornerShape(12.dp))
                                .padding(12.dp)
                                .heightIn(min = 72.dp) // <-- Same height
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Savings,
                                    contentDescription = stringResource(R.string.presentation_screen_content_description_savings),
                                    tint = Color(0xFFFFA500),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = stringResource(R.string.presentation_screen_save),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = stringResource(
                                            R.string.presentation_screen_annual_savings_text,
                                            yearlySavings
                                        ), color = Color.White
                                    )
                                }
                            }
                        }
                    }
                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )


                    // Choose month
                    Text(
                        text = stringResource(R.string.presentation_screen_choose_month),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .background(
                                    brush = gradient_button, shape = RoundedCornerShape(12.dp)
                                ) // Gradient here
                        ) {

                            OutlinedTextField(
                                value = monthNames[selectedMonthIndex],
                                onValueChange = {},
                                readOnly = true,
                                placeholder = {
                                    Text(
                                        text = stringResource(R.string.presentation_screen_choose_month),
                                        color = Color.White,
                                        fontStyle = FontStyle.Italic
                                    )
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .background(Color.Transparent),
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = stringResource(R.string.presentation_screen_content_description_month_drop_down),
                                        tint = Color.White
                                    )
                                },
                                textStyle = LocalTextStyle.current.copy(color = Color.White),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(Color(0xFF052B40))
                        ) {
                            monthNames.forEachIndexed { index, month ->
                                DropdownMenuItem(text = {
                                    Text(
                                        text = month,
                                        color = Color.White,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }, onClick = {
                                    selectedMonthIndex = index
                                    expanded = false
                                })
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    WeatherBoxRow(temp, snow, cloud)
                    Spacer(modifier = Modifier.height(20.dp))

                    //Energy produced according to month selected
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF081F2D), shape = RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                stringResource(
                                    R.string.presentation_screen_solar_radiation_text, avgRad
                                ),
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(10.dp)
                            )

                            Text(
                                stringResource(
                                    R.string.presentation_screen_produced_energy_text,
                                    producedPower,
                                    monthNames[selectedMonthIndex]
                                ),
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Box(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            "Energiproduksjon utover året",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    MonthlyBarChart(powerProductionList, monthNames)

                    Box(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Spacer(modifier = Modifier.height(30.dp))

                        Text(
                            text = stringResource(
                                R.string.presentation_screen_monthly_savings_text,
                                chosenMonthSavings,
                                monthNames[selectedMonthIndex]
                            ), color = Color.White, fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    MonthlySavingsChart(monthlySavings, monthNames)
                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    // Calculate and show time it takes to pay for sun panel installation
                    val installationCost =
                        (panelSize?.times(3500)) // Average 3500kr per square meter (estimate)
                    val timeToPayBack =
                        if (yearlySavings > 0) installationCost?.div(yearlySavings) else null

                    timeToPayBack?.let {
                        // Yearly savings
                        InfoBox(
                            icon = Icons.Filled.Savings,
                            iconColor = Color(0xFFFFA500),
                            text = stringResource(
                                R.string.presentation_screen_annual_savings_info_text,
                                producedPower,
                                monthNames[selectedMonthIndex],
                                powerCostForChosenMonth,
                                chosenMonthSavings
                            )
                            // Time it takes to pay back
                        )
                        InfoBox(
                            icon = Icons.Filled.BarChart,
                            iconColor = Color(0xFFFFA500),
                            text = stringResource(
                                R.string.presentation_screen_repayment_period_info_text,
                                yearlyProduction,
                                yearlySavings,
                                timeToPayBack
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Examples(
                                icon = Icons.Filled.Tv,
                                iconColor = Color.DarkGray,
                                text = stringResource(
                                    R.string.presentation_screen_example_tv, yearlyProduction / 0.05
                                )
                            )

                            Examples(
                                icon = Icons.Filled.Dining,
                                iconColor = Color.Magenta,
                                text = stringResource(
                                    R.string.presentation_screen_example_cooking,
                                    yearlyProduction / 60.0
                                )
                            )
                            Examples(
                                icon = Icons.Filled.CarRepair,
                                iconColor = Color.Green,
                                text = stringResource(
                                    R.string.presentation_screen_example_ev, yearlyProduction / 0.24
                                )
                            )
                            Examples(
                                icon = Icons.Filled.Cabin,
                                iconColor = Color(0xFF794736),
                                text = stringResource(
                                    R.string.presentation_screen_example_cabin,
                                    yearlyProduction / 675
                                )
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    } else if (uiState is SunEnergyUiState.Error || powerUiState is PowerUiState.Error) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF000000)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.presentation_feilmelding),
                modifier = Modifier.padding(16.dp),
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF000000)),
            contentAlignment = Alignment.Center
        ) {
            val composition by rememberLottieComposition(
                LottieCompositionSpec.Asset(
                    stringResource(R.string.presentation_screen_loading_animation)
                )
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradient),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    LottieAnimation(
                        composition = composition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier.size(200.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.general_loading),
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun InfoBox(icon: ImageVector, iconColor: Color, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .background(Color(0xFF081F2D), shape = RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.Top)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text, color = Color.White, fontSize = 14.sp
        )
    }
}


// MonthlySavingsChart - Diagram for cost savings
@Composable
fun MonthlySavingsChart(savings: List<Double>, monthNames: List<String>) {
    val maximumValue = savings.maxOrNull()?.takeIf { it > 0 }?.let {
        when {
            it <= 50 -> 50
            it <= 100 -> 100
            it <= 250 -> 250
            it <= 500 -> 500
            it <= 1000 -> 1000
            else -> ((it / 500).toInt() + 1) * 500
        }
    } ?: 100

    val step = maximumValue / 5

    Row(modifier = Modifier.fillMaxWidth()) {

        Column(
            modifier = Modifier
                .padding(end = 4.dp)
                .height(250.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End
        ) {
            for (i in 5 downTo 0) {
                Text(
                    text = "${(i * step)}",
                    fontSize = 12.sp,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(4.dp))

        Column(modifier = Modifier.weight(1f)) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                val barWidth = size.width / (savings.size * 2)
                val spacing = barWidth

                for (i in 0..5) {
                    val y = size.height - (i * size.height / 5f)
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.5f),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }


                savings.forEachIndexed { index, saving ->
                    val x = index * (barWidth + spacing)
                    val savingHeight = (saving.toFloat() / maximumValue.toFloat()) * size.height

                    val graph_gradient = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF16E350),
                            Color(0xFF14B843),
                            Color(0xFF034E28),
                        )
                    )

                    drawRoundRect(
                        brush = graph_gradient,
                        topLeft = Offset(x, size.height - savingHeight),
                        size = Size(barWidth, savingHeight),
                        cornerRadius = CornerRadius(x = 30f, y = 30f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                monthNames.forEach {
                    Text(text = it, fontSize = 12.sp, color = Color.White)
                }
            }
            // Text under graph for energy production
            Text(
                text = stringResource(R.string.presentation_screen_months),
                fontSize = 12.sp,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 4.dp, bottom = 16.dp)
            )
        }
    }
}

// Draw a adjusted diagram over power production per month
@Composable
fun MonthlyBarChart(powerProduction: List<Double>, monthNames: List<String>) {
    val maximumValue = powerProduction.maxOrNull()?.takeIf { it > 0 }?.let {
        when {
            it <= 100 -> 100
            it <= 250 -> 250
            it <= 500 -> 500
            it <= 1000 -> 1000
            it <= 1500 -> 1500
            else -> ((it / 500).toInt() + 1) * 500
        }
    } ?: 100

    val step = maximumValue / 5

    Row(modifier = Modifier.fillMaxWidth()) {

        Column(
            modifier = Modifier
                .padding(end = 4.dp)
                .height(250.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End
        ) {
            for (i in 5 downTo 0) {
                Text(
                    text = "${(i * step)}",
                    fontSize = 12.sp,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(4.dp))

        Column(modifier = Modifier.weight(1f)) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {

                val barWidth = size.width / (powerProduction.size * 2)
                val spacing = barWidth

                for (i in 0..5) {
                    val y = size.height - (i * size.height / 5f)
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.5f),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                powerProduction.forEachIndexed { index, strøm ->
                    val x = index * (barWidth + spacing) + barWidth / 2
                    val powerH = (strøm.toFloat() / maximumValue.toFloat()) * size.height

                    val graph_gradient = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFEF453),
                            Color(0xFFF76E09),
                            Color(0xFFE3401A),
                        )
                    )


                    drawRoundRect(
                        brush = graph_gradient,
                        topLeft = Offset(x - barWidth / 2, size.height - powerH),
                        size = Size(barWidth, powerH),
                        cornerRadius = CornerRadius(x = 30f, y = 30f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                monthNames.forEach {
                    Text(text = it, fontSize = 12.sp, color = Color.White)
                }
            }

            Text(
                text = stringResource(R.string.presentation_screen_months),
                fontSize = 12.sp,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 4.dp, bottom = 10.dp)
            )
        }
        Spacer(
            modifier = Modifier.height(16.dp)
        )
    }
}


@Composable
fun WeatherBox(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    Column(
        modifier = modifier
            .heightIn(min = 100.dp)
            .background(
                color = Color(0xFF081F2D), shape = RoundedCornerShape(20.dp)
            )
            .padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, fontWeight = FontWeight.Bold, color = Color.White)
        Text(value, fontSize = 16.sp, color = Color.White)
        subtitle?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(it, fontSize = 12.sp, color = Color.White)
        }
    }
}


@SuppressLint("DefaultLocale")
@Composable
fun WeatherBoxRow(
    temperature: Double?,
    snowDepth: Double?,
    cloudCover: Double?,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        WeatherBox(title = stringResource(R.string.presentation_screen_temp_title),
            value = "${
                temperature?.let {
                    String.format(
                        "%.1f", it
                    )
                } ?: "-"
            } ${stringResource(R.string.general_unit_celsius)}",
            modifier = Modifier
                .weight(1f)
                .background(Color(0xFF052B40)))

        val snowText = when {
            snowDepth == null -> "-"
            snowDepth < 5 -> stringResource(R.string.presentation_screen_snow_light)
            snowDepth < 15 -> stringResource(R.string.presentation_screen_snow_moderate)
            else -> stringResource(R.string.presentation_screen_snow_heavy)
        }
        WeatherBox(title = stringResource(R.string.presentation_screen_snow_title),
            value = "${
                snowDepth?.let {
                    String.format(
                        "%.1f", it
                    )
                } ?: "-"
            } ${stringResource(R.string.general_unit_cm)}",
            subtitle = snowText,
            modifier = Modifier
                .weight(1f)
                .background(Color(0xFF052B40)))

        val skyText = when {
            cloudCover == null -> "-"
            cloudCover <= 3 -> stringResource(R.string.presentation_screen_sky_clear)
            cloudCover <= 6 -> stringResource(R.string.presentation_screen_sky_partly_cloudy)
            else -> stringResource(R.string.presentation_screen_sky_overcast)
        }
        WeatherBox(title = stringResource(R.string.presentation_screen_cloud_cover_title),
            value = "${
                cloudCover?.let {
                    String.format(
                        "%.1f", it
                    )
                } ?: "-"
            } ${stringResource(R.string.general_unit_octas)}",
            subtitle = skyText,
            modifier = Modifier
                .weight(1f)
                .background(Color(0xFF052B40)))
    }
}

@Composable
fun Examples(icon: ImageVector, iconColor: Color, text: String) {
    Box(
        modifier = Modifier
            .height(250.dp)
            .width(150.dp)
            .background(Color(0xFF052B40))
            .border(1.dp, Color.White, shape = RoundedCornerShape(20.dp)),

        ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.TopCenter)
                .padding(bottom = 16.dp, top = 16.dp)
        )
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp, top = 80.dp)
        )
    }
    Spacer(modifier = Modifier.width(20.dp))
}