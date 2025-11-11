package no.uio.ifi.in2000.joakimcm.solcelleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import no.uio.ifi.in2000.joakimcm.solcelleapp.ui.theme.PanelPlanAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PanelPlanAppTheme {
                PanelPlanApp()
            }
        }
    }
}



