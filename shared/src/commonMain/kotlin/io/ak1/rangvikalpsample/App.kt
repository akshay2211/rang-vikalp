package io.ak1.rangvikalpsample

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.ak1.rangvikalp.RangVikalp
import io.ak1.rangvikalp.defaultSelectedColor
import kotlinx.coroutines.launch

@Composable
@Preview
fun App() {
    MaterialTheme {
        val coroutine = rememberCoroutineScope()
        val color = remember { Animatable(defaultSelectedColor) }
        var pickerVisible by remember { mutableStateOf(true) }
        var shadesVisible by remember { mutableStateOf(false) }
        var lightMode by remember { mutableStateOf(true) }
        val chevronRotation by animateFloatAsState(
            targetValue = if (pickerVisible) 180f else 0f
        )

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .safeContentPadding()
                .fillMaxSize()
        ) {
            Surface(
                color = color.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "RangVikalp",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = shadesVisible,
                    onClick = { shadesVisible = !shadesVisible },
                    label = { Text("Shades") }
                )
                FilterChip(
                    selected = lightMode,
                    onClick = { lightMode = !lightMode },
                    label = { Text(if (lightMode) "Light" else "Dark") }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = { pickerVisible = !pickerVisible }) {
                    Text(
                        text = "▲",
                        color = color.value,
                        modifier = Modifier.rotate(chevronRotation)
                    )
                    Text(
                        text = if (pickerVisible) "  Hide picker" else "  Show picker",
                        color = color.value
                    )
                }
            }

            RangVikalp(
                isVisible = pickerVisible,
                showShades = shadesVisible,
                colorIntensity = if (lightMode) 7 else 3
            ) { picked ->
                coroutine.launch {
                    color.animateTo(picked, animationSpec = tween(600))
                }
            }
        }
    }
}