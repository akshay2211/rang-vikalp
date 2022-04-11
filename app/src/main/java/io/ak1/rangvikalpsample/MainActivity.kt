package io.ak1.rangvikalpsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.ak1.rangvikalp.RangVikalp
import io.ak1.rangvikalpsample.ui.theme.RangVikalpSampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RangVikalpSampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}
//Initial sample added to built functionality upon
@Composable
fun Greeting(name: String) {
    var isVisible by remember {
        mutableStateOf(true)
    }
    Column(
        Modifier
            .padding(16.dp)
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, true)
        )
        Button(onClick = {
            isVisible = !isVisible
        }) {
            Text(text = "Hello $name!")
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        )
        RangVikalp(isVisible = isVisible) {
        }

    }


}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    RangVikalpSampleTheme {
        Greeting("Android")
    }
}