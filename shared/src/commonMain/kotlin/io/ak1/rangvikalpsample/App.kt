package io.ak1.rangvikalpsample

import androidx.compose.animation.Animatable
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.ak1.rangvikalp.RangVikalp
import io.ak1.rangvikalp.defaultRangVikalpColors
import io.ak1.rangvikalp.defaultRangVikalpPresets
import io.ak1.rangvikalp.rememberRangVikalpState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Composable
@Preview
fun App() {
    MaterialTheme {
        val scope = rememberCoroutineScope()
        var lightMode by remember { mutableStateOf(false) }
        val pickerState = rememberRangVikalpState(defaultRangVikalpPresets[4])
        val preview = remember { Animatable(pickerState.color) }

        LaunchedEffect(pickerState) {
            snapshotFlow { pickerState.color }
                .distinctUntilChanged()
                .collect { picked ->
                    scope.launch { preview.animateTo(picked, tween(180)) }
                }
        }

        val theme = defaultRangVikalpColors(dark = !lightMode)

        Column(
            modifier = Modifier
                .background(theme.surface)
                .safeContentPadding()
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Surface(
                color = preview.value,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "#${pickerState.hex6}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = lightMode,
                    onClick = { lightMode = !lightMode },
                    label = { Text(if (lightMode) "Light" else "Dark") },
                )
            }
            Spacer(Modifier.weight(1f))
            RangVikalp(
                state  = pickerState,
                colors = theme,
            )
        }
    }
}