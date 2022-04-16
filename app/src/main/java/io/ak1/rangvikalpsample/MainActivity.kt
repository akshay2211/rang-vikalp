package io.ak1.rangvikalpsample

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import io.ak1.rangvikalpsample.ui.components.RangVikalpComposable
import io.ak1.rangvikalpsample.ui.theme.RangVikalpSampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            RangVikalpSampleTheme {
                ProvideWindowInsets {
                    val systemUiController = rememberSystemUiController()
                    val darkIcons = MaterialTheme.colors.isLight
                    SideEffect {
                        systemUiController.setSystemBarsColor(
                            Color.Transparent,
                            darkIcons = darkIcons
                        )
                    }
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {
                        RangVikalpComposable()
                    }
                }
            }
        }
    }
}


@Preview("Rang Vikalp")
@Preview("Rang Vikalp (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("Rang Vikalp (big font)", fontScale = 1.5f)
@Preview("Rang Vikalp (large screen)", device = Devices.PIXEL_C)
@Composable
fun DefaultPreview() {
    RangVikalpSampleTheme {
        RangVikalpComposable()
    }
}