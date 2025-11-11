package no.uio.ifi.in2000.joakimcm.solcelleapp.ui.screens.info


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import no.uio.ifi.in2000.joakimcm.solcelleapp.R
import no.uio.ifi.in2000.joakimcm.solcelleapp.ui.theme.PanelPlanAppTheme
import no.uio.ifi.in2000.joakimcm.solcelleapp.ui.theme.gradient

@Preview
@Composable
fun InfoScreen() {
    PanelPlanAppTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        {
            Text(
                text = stringResource(R.string.info_screen_title),
                color = Color.White,
                modifier = Modifier
                    .padding(top = 30.dp, bottom = 10.dp)
                    .align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.titleLarge

            )

            Spacer(modifier = Modifier.height(40.dp))
            LazyColumn(
                // Container for all the info items
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                gradient, shape = RoundedCornerShape(
                                    topStart = 20.dp, topEnd = 20.dp
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Column {
                            InfoText(
                                R.string.info_screen_tax_body,
                                R.string.info_screen_tax_title,
                                R.string.info_screen_tax_animation,
                                R.string.info_screen_tax_reference
                            )
                            InfoText(
                                R.string.info_screen_profit_body,
                                R.string.info_screen_profit_title,
                                R.string.info_screen_profit_animation,
                                R.string.info_screen_profit_reference
                            )
                            InfoText(
                                R.string.info_screen_price_body,
                                R.string.info_screen_price_title,
                                R.string.info_screen_price_animation,
                                R.string.info_screen_price_reference
                            )
                            InfoText(
                                R.string.info_screen_general_body,
                                R.string.info_screen_general_title,
                                R.string.info_screen_general_animation,
                                R.string.info_screen_general_reference
                            )
                            InfoText(
                                R.string.info_screen_advantages_body,
                                R.string.info_screen_advantages_title,
                                R.string.info_screen_advantages_animation,
                                R.string.info_screen_advantages_reference
                            )
                            InfoText(
                                R.string.info_screen_types_body,
                                R.string.info_screen_types_title,
                                R.string.info_screen_types_animation,
                                R.string.info_screen_types_reference
                            )
                            InfoText(
                                R.string.info_screen_conditions_body,
                                R.string.info_screen_conditions_title,
                                R.string.info_screen_conditions_animation,
                                R.string.info_screen_conditions_reference
                            )
                            InfoText(
                                R.string.info_screen_enova_body,
                                R.string.info_screen_enova_title,
                                R.string.info_screen_enova_animation,
                                R.string.info_screen_enova_reference
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoText(
    bodyId: Int, titleId: Int, animationAssetId: Int,
    reference: Int,
    // e.g. "lonnsomhet.json"
) {
    var expanded by remember { mutableStateOf(false) }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset(
            stringResource(
                animationAssetId
            )
        )
    )

    Box(
        modifier = Modifier
            .shadow(
                elevation = 20.dp, spotColor = Color.Black, shape = RoundedCornerShape(8.dp)
            )
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF02719E), Color(0xFF052B40), Color(0xFF102837)
                    )
                ),

                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(8.dp)) {
                LottieAnimation(
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier.size(100.dp)
                )
                Text(
                    text = stringResource(titleId),
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
            }
            AnimatedVisibility(visible = expanded) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            text = stringResource(id = bodyId),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White,
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                        )
                        Text(
                            text = stringResource(id = reference),
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier
                                .padding(start = 16.dp, top = 8.dp)
                                .align(Alignment.End)
                        )
                    }

                }
            }
        }

    }

    Spacer(modifier = Modifier.height(20.dp))
}


























