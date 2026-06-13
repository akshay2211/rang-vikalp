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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * RangVikalp — an HSV color picker.
 *
 * Modeled after a modern design-tool color picker:
 *  • Saturation/Value box with draggable thumb
 *  • Hue + Alpha sliders
 *  • Eyedropper button + HEX value + opacity %
 *  • Preset color palette + shuffle
 *
 * State is hoisted via [RangVikalpState] so the host can read the live color,
 * react to changes, or push a new color into the picker programmatically.
 * Every internal piece ([SaturationValueBox], [HueSlider], [AlphaSlider],
 * [EyedropperButton], [HexRow], [PresetsRow]) is also exposed individually for
 * custom layouts.
 *
 * @param state         hoisted picker state; create with [rememberRangVikalpState]
 * @param presets       row of quick-pick swatches at the bottom
 * @param colors        light/dark theming for the picker chrome
 * @param onEyedropper  invoked when the eyedropper button is tapped. Color
 *                      sampling is platform-specific so the host implements
 *                      it and calls [RangVikalpState.setFromColor] when done.
 *                      Null hides the eyedropper button entirely.
 * @param onColorChange called whenever the picked color changes
 */
@Composable
fun RangVikalp(
    modifier: Modifier = Modifier,
    state: RangVikalpState = rememberRangVikalpState(),
    presets: List<Color> = defaultRangVikalpPresets,
    colors: RangVikalpColors = defaultRangVikalpColors(),
    onColorChange: (Color) -> Unit = {},
) {
    // Emit only when the composed color actually changes — avoid duplicate
    // callbacks while a drag is in progress on the same axis.
    LaunchedEffect(state) {
        snapshotFlow { state.color }
            .distinctUntilChanged()
            .collect { onColorChange(it) }
    }

    PickerCard(colors = colors, modifier = modifier) {
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
        Spacer(Modifier.height(12.dp))
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