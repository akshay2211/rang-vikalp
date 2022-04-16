package io.ak1.rangvikalpsample

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import io.ak1.rangvikalp.RangVikalp
import io.ak1.rangvikalp.colorArray
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
                        Demo()
                    }
                }
            }
        }
    }
}


@Composable
fun Demo() {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    var isVisible by remember {
        mutableStateOf(true)
    }
    var color by remember {
        mutableStateOf(colorArray[0][5])
    }
    var minimalView by remember {
        mutableStateOf(true)
    }
    var intensity by remember {
        mutableStateOf(5)
    }
    val floatAnimateAsState by animateFloatAsState(
        targetValue = if (isVisible) 0f else 180f
    )
    Scaffold(modifier = Modifier.statusBarsPadding(),
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { isVisible = !isVisible }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_chevron_up),
                            contentDescription = "",
                            tint = color,
                            modifier = Modifier
                                .padding(12.dp)
                                .rotate(floatAnimateAsState)
                        )
                    }
                }
                RangVikalp(
                    isVisible = isVisible,
                    showShades = !minimalView
                ) {
                    color = it
                }
            }

        }) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(6.dp)) {
            Text(text = "Minimal View", modifier = Modifier.padding(end = 6.dp))
            Switch(checked = minimalView, onCheckedChange = {
                minimalView = !minimalView
            }, colors = SwitchDefaults.colors(checkedThumbColor = color))
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 200.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = { isVisible = !isVisible }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_color),
                    contentDescription = "",
                    tint = color,
                    modifier = Modifier
                        .size(200.dp)
                        .padding(4.dp)
                )
            }
        }

    }
}
@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            // restore original orientation when view disappears
            activity.requestedOrientation = originalOrientation
        }
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    RangVikalpSampleTheme {
        Demo()
    }
}