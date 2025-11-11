package no.uio.ifi.in2000.joakimcm.solcelleapp.ui.screens.startscreen


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay
import no.uio.ifi.in2000.joakimcm.solcelleapp.ui.theme.gradient


@Composable


fun StartScreen(
    onNavigateToFront: () -> Unit,
    modifier: Modifier = Modifier
        .fillMaxSize()
        .background(gradient),
) {
    LaunchedEffect(Unit) {
        //delay(100)
        delay(5500)
        onNavigateToFront()
    }
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("Scene-1-4.json")
    )


    val progress by animateLottieCompositionAsState(
        composition = composition, iterations = LottieConstants.IterateForever
    )

    Box(
        modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
    )

    {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
