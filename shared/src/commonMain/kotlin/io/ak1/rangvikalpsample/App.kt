package io.ak1.rangvikalpsample

import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ak1.rangvikalp.AlphaSlider
import io.ak1.rangvikalp.ArcAlphaSlider
import io.ak1.rangvikalp.ArcHueSlider
import io.ak1.rangvikalp.HexRow
import io.ak1.rangvikalp.HueSlider
import io.ak1.rangvikalp.PresetSwatches
import io.ak1.rangvikalp.PresetsRow
import io.ak1.rangvikalp.RangVikalp
import io.ak1.rangvikalp.RangVikalpColors
import io.ak1.rangvikalp.RangVikalpState
import io.ak1.rangvikalp.SaturationValueBox
import io.ak1.rangvikalp.SaturationValueCircle
import io.ak1.rangvikalp.colorArray
import io.ak1.rangvikalp.defaultRangVikalpColors
import io.ak1.rangvikalp.defaultRangVikalpPresets
import io.ak1.rangvikalp.rememberRangVikalpState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/** Catalogue of compositions to demo. */
private enum class Variation(val title: String, val blurb: String) {
    Tabbed     ("Tabbed picker",    "Default RangVikalp — Preset & Custom tabs"),
    Linear     ("Square + linear",  "SaturationValueBox + HueSlider + AlphaSlider + HexRow"),
    Circular   ("Circular stack",   "SaturationValueCircle stacked under ArcHueSlider + ArcAlphaSlider"),
    PresetsOnly("Presets only",     "PresetSwatches alone — no sliders, no SV box"),
    QuickPick  ("Quick pick row",   "PresetsRow + HexRow — minimal one-line picker"),
    SlidersOnly("Sliders only",     "HueSlider + AlphaSlider — colour by sliders alone"),
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        // Shared state persists across menu ↔ detail navigation, so the live
        // colour you picked in one sample is still there when you back out
        // and open another.
        val state = rememberRangVikalpState(defaultRangVikalpPresets[4])
        var lightMode by remember { mutableStateOf(false) }
        var route by remember { mutableStateOf<Variation?>(null) }
        val theme = defaultRangVikalpColors(dark = !lightMode)

        // Smooth colour for header / menu swatch.
        val scope = rememberCoroutineScope()
        val preview = remember { Animatable(state.color) }
        LaunchedEffect(state) {
            snapshotFlow { state.color }
                .distinctUntilChanged()
                .collect { picked -> scope.launch { preview.animateTo(picked, tween(180)) } }
        }

        Box(
            modifier = Modifier
                .background(theme.surface)
                .fillMaxSize(),
        ) {
            AnimatedContent(
                targetState = route,
                transitionSpec = {
                    val goingDeeper = targetState != null
                    val dir = if (goingDeeper) 1 else -1
                    (slideInHorizontally { it * dir } + fadeIn()) togetherWith
                        (slideOutHorizontally { -it * dir } + fadeOut())
                },
                label = "route",
            ) { current ->
                when (current) {
                    null -> MenuScreen(
                        previewColor   = preview.value,
                        hex            = state.hex6,
                        theme          = theme,
                        lightMode      = lightMode,
                        onToggleTheme  = { lightMode = !lightMode },
                        onSelect       = { route = it },
                    )
                    else -> DetailScreen(
                        variation = current,
                        state     = state,
                        theme     = theme,
                        onBack    = { route = null },
                    )
                }
            }
        }
    }
}

/* ──────────────────────────────────────────────────────────────────────────
 *  Menu screen
 * ────────────────────────────────────────────────────────────────────────── */

@Composable
private fun MenuScreen(
    previewColor: Color,
    hex: String,
    theme: RangVikalpColors,
    lightMode: Boolean,
    onToggleTheme: () -> Unit,
    onSelect: (Variation) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            MenuHeader(
                color         = previewColor,
                hex           = hex,
                lightMode     = lightMode,
                onToggleTheme = onToggleTheme,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Samples",
                color = theme.onSurface,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        items(Variation.entries) { v ->
            MenuItem(
                variation    = v,
                previewColor = previewColor,
                theme        = theme,
                onClick      = { onSelect(v) },
            )
        }
    }
}

@Composable
private fun MenuHeader(
    color: Color,
    hex: String,
    lightMode: Boolean,
    onToggleTheme: () -> Unit,
) {
    Surface(
        color = color,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "RangVikalp",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Multi-platform Compose colour picker",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "#$hex",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                )
                FilterChip(
                    selected = lightMode,
                    onClick  = onToggleTheme,
                    label    = { Text(if (lightMode) "Light" else "Dark") },
                )
            }
        }
    }
}

@Composable
private fun MenuItem(
    variation: Variation,
    previewColor: Color,
    theme: RangVikalpColors,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(theme.surfaceInset)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(previewColor),
        )
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = variation.title,
                color = theme.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = variation.blurb,
                color = theme.onSurfaceMuted,
                fontSize = 12.sp,
            )
        }
        Canvas(Modifier.size(14.dp)) { drawChevronRight(theme.onSurfaceMuted) }
    }
}

/* ──────────────────────────────────────────────────────────────────────────
 *  Detail screen
 * ────────────────────────────────────────────────────────────────────────── */

@Composable
private fun DetailScreen(
    variation: Variation,
    state: RangVikalpState,
    theme: RangVikalpColors,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding(),
    ) {
        TopBar(title = variation.title, theme = theme, onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text  = variation.blurb,
                color = theme.onSurfaceMuted,
                fontSize = 13.sp,
            )
            when (variation) {
                Variation.Tabbed      -> TabbedSample(state, theme)
                Variation.Linear      -> LinearSample(state, theme)
                Variation.Circular    -> CircularSample(state, theme)
                Variation.PresetsOnly -> PresetsOnlySample(state, theme)
                Variation.QuickPick   -> QuickPickSample(state, theme)
                Variation.SlidersOnly -> SlidersOnlySample(state, theme)
            }
        }
    }
}

@Composable
private fun TopBar(title: String, theme: RangVikalpColors, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(Modifier.size(16.dp)) { drawChevronLeft(theme.onSurface) }
        }
        Spacer(Modifier.width(4.dp))
        Text(
            text = title,
            color = theme.onSurface,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

/* ──────────────────────────────────────────────────────────────────────────
 *  Variations (one composable per Variation entry)
 * ────────────────────────────────────────────────────────────────────────── */

@Composable
private fun TabbedSample(state: RangVikalpState, theme: RangVikalpColors) {
    RangVikalp(state = state, colors = theme)
}

@Composable
private fun LinearSample(state: RangVikalpState, theme: RangVikalpColors) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(theme.surface)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SaturationValueBox(
            state    = state,
            modifier = Modifier.fillMaxWidth().aspectRatio(1.4f),
        )
        HueSlider(state = state)
        AlphaSlider(state = state)
        HexRow(state = state, colors = theme)
    }
}

@Composable
private fun CircularSample(state: RangVikalpState, theme: RangVikalpColors) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(theme.surface)
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .padding(4.dp),
            contentAlignment = Alignment.Center,
        ) {
            // Outer ring first so the inner disk receives center taps first.
            ArcHueSlider(state = state, modifier = Modifier.fillMaxSize())
            ArcAlphaSlider(state = state, modifier = Modifier.fillMaxSize(0.78f))
            SaturationValueCircle(state = state, modifier = Modifier.fillMaxSize(0.56f))
        }
        HexRow(state = state, colors = theme)
    }
}

@Composable
private fun PresetsOnlySample(state: RangVikalpState, theme: RangVikalpColors) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(theme.surface)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        PresetSwatches(state = state, families = colorArray, colors = theme)
        HexRow(state = state, colors = theme)
    }
}

@Composable
private fun QuickPickSample(state: RangVikalpState, theme: RangVikalpColors) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(theme.surface)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        PresetsRow(state = state, presets = defaultRangVikalpPresets, colors = theme)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(state.color),
            )
            Spacer(Modifier.width(12.dp))
            Box(Modifier.weight(1f)) {
                HexRow(state = state, colors = theme)
            }
        }
    }
}

@Composable
private fun SlidersOnlySample(state: RangVikalpState, theme: RangVikalpColors) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(theme.surface)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Surface(
            color = state.color,
            modifier = Modifier.fillMaxWidth().height(72.dp),
            shape = RoundedCornerShape(16.dp),
        ) { Box(Modifier.fillMaxSize()) }
        HueSlider(state = state)
        AlphaSlider(state = state)
        HexRow(state = state, colors = theme)
    }
}

/* ──────────────────────────────────────────────────────────────────────────
 *  Canvas chevrons (no extra drawable resources needed)
 * ────────────────────────────────────────────────────────────────────────── */

private fun DrawScope.drawChevronRight(color: Color) {
    val w = size.width; val h = size.height
    val path = Path().apply {
        moveTo(w * 0.35f, h * 0.15f)
        lineTo(w * 0.70f, h * 0.50f)
        lineTo(w * 0.35f, h * 0.85f)
    }
    drawPath(
        path  = path,
        color = color,
        style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
    )
}

private fun DrawScope.drawChevronLeft(color: Color) {
    val w = size.width; val h = size.height
    val path = Path().apply {
        moveTo(w * 0.65f, h * 0.15f)
        lineTo(w * 0.30f, h * 0.50f)
        lineTo(w * 0.65f, h * 0.85f)
    }
    drawPath(
        path  = path,
        color = color,
        style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
    )
}
