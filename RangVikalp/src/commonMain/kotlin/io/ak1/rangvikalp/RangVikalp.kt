/*
 * Copyright (C) 2022 akshay2211 (Akshay Sharma)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package io.ak1.rangvikalp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged

/** Which view the [RangVikalp] picker is currently showing. */
enum class RangVikalpTab(val label: String) {
    Preset("Preset"),
    Custom("Custom"),
}

/**
 * RangVikalp — a tabbed HSV color picker.
 *
 *  • **Preset** tab: a grid of color families ([presetGroups]) with an
 *    expanding row of shades for the active family.
 *  • **Custom** tab: full HSV picker — SV box, hue + alpha sliders, hex,
 *    opacity %, and a quick-pick preset row.
 *
 * State is hoisted via [RangVikalpState] so the host can read the live color,
 * react to changes, or push a new color into the picker programmatically.
 * Every internal piece ([TabStrip], [PresetSwatches], [SaturationValueBox],
 * [HueSlider], [AlphaSlider], [HexRow], [PresetsRow]) is also exposed
 * individually for custom layouts.
 *
 * @param state          hoisted picker state; create with [rememberRangVikalpState]
 * @param presets        bottom row swatches shown in the Custom tab
 * @param presetGroups   color families shown in the Preset tab (defaults to the
 *                       full Material palette from [colorArray])
 * @param colors         light/dark theming for the picker chrome
 * @param initialTab     which tab the picker opens on
 * @param showTabs       set false to hide the tab strip and render only [initialTab]
 * @param onColorChange  called whenever the picked color changes
 */
@Composable
fun RangVikalp(
    modifier: Modifier = Modifier,
    state: RangVikalpState = rememberRangVikalpState(),
    presets: List<Color> = defaultRangVikalpPresets,
    presetGroups: List<List<Color>> = colorArray,
    colors: RangVikalpColors = defaultRangVikalpColors(),
    initialTab: RangVikalpTab = RangVikalpTab.Preset,
    showTabs: Boolean = true,
    onColorChange: (Color) -> Unit = {},
) {
    // Emit only when the composed color actually changes — avoid duplicate
    // callbacks while a drag is in progress on the same axis.
    LaunchedEffect(state) {
        snapshotFlow { state.color }
            .distinctUntilChanged()
            .collect { onColorChange(it) }
    }

    var selectedTab by remember { mutableStateOf(initialTab) }

    PickerCard(colors = colors, modifier = modifier) {
        if (showTabs) {
            TabStrip(
                tabs = RangVikalpTab.entries.map { it.label },
                selectedIndex = selectedTab.ordinal,
                colors = colors,
                onSelect = { selectedTab = RangVikalpTab.entries[it] },
            )
            Spacer(Modifier.height(14.dp))
        }
        // Both tab views share the same minimum height so the picker doesn't
        // resize when the user flips between Preset and Custom. The Custom
        // view is the taller one (its SV box is square = matches the card
        // width), so we derive the floor from `width + everythingBelowSv`.
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val sharedMinHeight = maxWidth + CustomViewExtraHeight
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = sharedMinHeight),
            ) {
                when (selectedTab) {
                    RangVikalpTab.Preset -> PresetView(
                        state         = state,
                        presetGroups  = presetGroups,
                        colors        = colors,
                        modifier      = Modifier.fillMaxHeight(),
                    )
                    RangVikalpTab.Custom -> CustomView(
                        state    = state,
                        presets  = presets,
                        colors   = colors,
                        modifier = Modifier.fillMaxHeight(),
                    )
                }
            }
        }
    }
}

// SV box is square (aspectRatio 1f → height = width). Everything below in the
// Custom view sums to ~168dp: 12 spacer + 54 slider stack + 12 + 48 hex +
// 12 + 30 presets row. Keep this in sync if you customize Custom view sizes.
private val CustomViewExtraHeight = 168.dp

/* ──────────────────────────────────────────────────────────────────────────
 *  Tab views
 * ────────────────────────────────────────────────────────────────────────── */

@Composable
private fun PresetView(
    state: RangVikalpState,
    presetGroups: List<List<Color>>,
    colors: RangVikalpColors,
    modifier: Modifier = Modifier,
) {
    // Top-anchored swatches + bottom-anchored hex row — any extra height
    // imposed by the shared-size wrapper is absorbed by the middle weight(1f).
    Column(modifier = modifier.fillMaxWidth()) {
        PresetSwatches(
            state    = state,
            families = presetGroups,
            colors   = colors,
        )
        Spacer(Modifier.weight(1f, fill = true).heightIn(min = 14.dp))
        HexRow(state = state, colors = colors)
    }
}

@Composable
private fun CustomView(
    state: RangVikalpState,
    presets: List<Color>,
    colors: RangVikalpColors,
    modifier: Modifier = Modifier,
) {
    // Same anchoring rule as PresetView: SV box + sliders pinned to top,
    // HexRow + PresetsRow pinned to bottom, weight(1f) absorbs any excess.
    // At the picker's natural minimum height the weight spacer collapses to
    // its 12.dp minimum, so the layout looks unchanged from before.
    Column(modifier = modifier.fillMaxWidth()) {
        SaturationValueBox(
            state    = state,
            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
        )
        Spacer(Modifier.height(12.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f),
            ) {
                HueSlider(state = state)
                AlphaSlider(state = state)
            }
        }
        Spacer(Modifier.weight(1f, fill = true).heightIn(min = 12.dp))
        HexRow(state = state, colors = colors)
        Spacer(Modifier.height(12.dp))
        PresetsRow(
            state    = state,
            presets  = presets,
            colors   = colors,
        )
    }
}

@Composable
private fun PickerCard(
    colors: RangVikalpColors,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(28.dp))
            .padding(14.dp),
    ) {
        Column { content() }
    }
}
