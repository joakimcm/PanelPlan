package no.uio.ifi.in2000.joakimcm.solcelleapp.ui.theme

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80, secondary = PurpleGrey80, tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40, secondary = PurpleGrey40, tertiary = Pink40
)

val gradient = Brush.verticalGradient( // Background theme for the app
    colors = listOf(
        Color(0xFF02719E),
        Color(0xFF012E46),
        Color(0xFF0B1820),
        Color(0xFF0B1820),
        Color(0xFF0B1820),
    )
)

val gradient_button = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF02719E), Color(0xFF052B40), Color(0xFF102837)
    )
)

@Composable
fun PanelPlanAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {

        MaterialTheme(
            colorScheme = colorScheme, typography = Typography, content = content
        )
    }
}