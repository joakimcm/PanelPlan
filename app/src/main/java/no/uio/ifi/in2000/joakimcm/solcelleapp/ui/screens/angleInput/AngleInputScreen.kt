package no.uio.ifi.in2000.joakimcm.solcelleapp.ui.screens.angleInput


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.joakimcm.solcelleapp.Presentation
import no.uio.ifi.in2000.joakimcm.solcelleapp.R
import no.uio.ifi.in2000.joakimcm.solcelleapp.domain.kalkulasjoner.roofAngle
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.angle.AngleScreenState
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.angle.Direction
import no.uio.ifi.in2000.joakimcm.solcelleapp.ui.theme.gradient
import no.uio.ifi.in2000.joakimcm.solcelleapp.ui.theme.gradient_button


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AngleInputScreen(
    onNavigateToPresentation: (Presentation) -> Unit,
    viewModel: AngleScreenViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    var showPictureSheet by remember { mutableStateOf(false) }

    val angleScreenState: AngleScreenState by viewModel.angleState.collectAsState()

    var expanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val directions = Direction.entries.toList()

    var heightError by remember { mutableStateOf<String?>(null) }
    var widthError by remember { mutableStateOf<String?>(null) }
    var panelSizeError by remember { mutableStateOf<String?>(null) }

    val heightErrorString = stringResource(R.string.angle_input_screen_wrong_height_error)
    val widthErrorString = stringResource(R.string.angle_input_screen_wrong_width_error)
    val areaErrorString = stringResource(R.string.angle_input_screen_wrong_area_error)

    when (uiState) {
        is AngleUiState.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        gradient
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is AngleUiState.Error -> {
            Text(
                text = stringResource(
                    R.string.general_error, (uiState as AngleUiState.Error).message
                ), color = Color.Red, modifier = Modifier.padding(16.dp)
            )
        }

        is AngleUiState.Success, AngleUiState.Idle -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradient)
                    .verticalScroll(scrollState)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        })
                    }

            ) {

                Spacer(modifier = Modifier.height(30.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.angle_input_screen_title),
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        showPictureSheet = true
                        scope.launch { sheetState.show() }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = stringResource(R.string.angle_input_screen_content_description_more_info),
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

                val containerColor = Color(0xFFDCF0FB)
                OutlinedTextField(
                    value = angleScreenState.width,
                    onValueChange = { viewModel.setWidth(it) },
                    textStyle = TextStyle(color = Color.Black),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = containerColor,
                        unfocusedContainerColor = containerColor,
                        disabledContainerColor = containerColor,
                    ),

                    label = {
                        Text(
                            stringResource(R.string.angle_input_screen_width_input),
                            color = Color.Black.copy(alpha = 0.5f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(20.dp)
                )
                if (widthError != null) {
                    Text(
                        text = widthError ?: "",
                        color = Color(0xFFD31A1A),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = angleScreenState.height,
                    onValueChange = { viewModel.setHeight(it) },
                    textStyle = TextStyle(color = Color.Black),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = containerColor,
                        unfocusedContainerColor = containerColor,
                        disabledContainerColor = containerColor,
                    ),

                    label = {
                        Text(
                            stringResource(R.string.angle_input_screen_height_input),
                            color = Color.Black.copy(alpha = 0.5f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(20.dp)
                )
                if (heightError != null) {
                    Text(
                        text = heightError ?: "",
                        color = Color(0xFFD31A1A),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(

                    value = angleScreenState.panelSize,
                    onValueChange = { viewModel.setPanelSize(it) },
                    textStyle = TextStyle(color = Color.Black),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = containerColor,
                        unfocusedContainerColor = containerColor,
                        disabledContainerColor = containerColor,
                    ),
                    label = {
                        Text(
                            stringResource(R.string.angle_input_screen_area_input),
                            color = Color.Black.copy(alpha = 0.5f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(20.dp)
                )
                if (panelSizeError != null) {
                    Text(
                        text = panelSizeError ?: "",
                        color = Color(0xFFD31A1A),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))


                // Dropdown menu to choose direction on roof
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = angleScreenState.direction.displayName,
                        onValueChange = {},
                        readOnly = true,  // Let user choose from list

                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .background(gradient_button, shape = RoundedCornerShape(20.dp))
                            .height(56.dp),


                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = stringResource(R.string.angle_input_screen_content_description_direction),
                                tint = Color.White
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF052B40),   // Dark grey while in focus
                            unfocusedContainerColor = Color.Transparent, // Match background while not in focus
                        ),
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        shape = RoundedCornerShape(20.dp),


                        )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color(0xFF002A40)

                            ),
                    ) {
                        directions.forEach { direction ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = direction.displayName,
                                        color = Color.White,
                                        modifier = Modifier.fillMaxWidth() // Fill width
                                    )
                                },
                                onClick = {
                                    expanded = false
                                    viewModel.setDirection(direction)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF102837)) // Dark grey for whole row

                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(R.string.angle_input_screen_tips),
                    color = Color.White,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 3.dp, bottom = 4.dp, start = 10.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))


                Button(
                    onClick = {
                        heightError = null
                        widthError = null
                        panelSizeError = null

                        val height = angleScreenState.height.toDoubleOrNull()
                        val width = angleScreenState.width.toDoubleOrNull()
                        val panelSize = angleScreenState.panelSize.toDoubleOrNull()

                        var haveErrors = false

                        if (height == null) {
                            heightError = heightErrorString
                            haveErrors = true
                        }
                        if (width == null) {
                            widthError = widthErrorString
                            haveErrors = true
                        }
                        if (panelSize == null) {
                            panelSizeError = areaErrorString
                            haveErrors = true
                        }

                        if (!haveErrors) {
                            val angle = roofAngle(height!!, width!!)
                            val presentationDetails = Presentation(
                                angle = angle,
                                areal = panelSize,
                                direction = angleScreenState.direction
                            )
                            onNavigateToPresentation(presentationDetails)
                        }

                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(60.dp),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(0.6f)
                        .padding(40.dp)
                ) {
                    Text(stringResource(R.string.angle_input_screen_calculate), color = Color.White)
                }
            }

            if (showPictureSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showPictureSheet = false },
                    sheetState = sheetState,
                    containerColor = Color(0xFF0C2636)
                ) {
                    // Drag handle
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color.White)
                        )
                    }

                    // Picture to show user how to measure
                    Image(
                        painter = painterResource(id = R.drawable.roof_info),
                        contentDescription = stringResource(R.string.angle_input_screen_content_description_house),
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}