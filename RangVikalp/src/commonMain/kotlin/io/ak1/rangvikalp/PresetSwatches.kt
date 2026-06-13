/*
 * Copyright (C) 2026 akshay2211 (Akshay Sharma)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package io.ak1.rangvikalp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Preset palette view — grid of color families (one representative swatch per
 * family) plus an expanding row that reveals shades of the active family.
 *
 * - Tapping a family swatch loads its representative shade into [state] and
 *   reveals that family's shades below.
 * - Tapping a shade loads it into [state].
 * - The currently picked color is ringed with [RangVikalpColors.accent].
 *
 * @param families e.g. [colorArray] — list of color-shade groups
 * @param representativeIndex which shade within each family is shown in the
 *                            top grid (defaults to the mid-tone)
 */
@Composable
fun PresetSwatches(
    state: RangVikalpState,
    families: List<List<Color>>,
    modifier: Modifier = Modifier,
    colors: RangVikalpColors = defaultRangVikalpColors(),
    columnsPerRow: Int = 5,
    representativeIndex: Int = 5,
    familySwatchSize: Dp = 24.dp,
    shadeSwatchSize: Dp = 22.dp,
) {
    if (families.isEmpty()) return
    val current = state.color.copy(alpha = 1f)

    // Seed the active family from the current color when possible, otherwise
    // fall back to the first family. Remembered so taps on shades don't
    // collapse the active family back to the auto-detected one.
    var activeFamily by remember {
        mutableStateOf(families.indexOfFirstFamily(current).coerceAtLeast(0))
    }
    // Re-snap to a matching family if the state was changed from outside.
    val detected = families.indexOfFirstFamily(current)
    if (detected >= 0 && families[activeFamily].none { it == current }) {
        activeFamily = detected
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Family grid — chunked into rows of [columnsPerRow]
        families.chunked(columnsPerRow).forEachIndexed { rowIndex, rowFamilies ->
            if (rowIndex > 0) Spacer(Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                rowFamilies.forEach { family ->
                    val rep = family[representativeIndex.coerceIn(0, family.lastIndex)]
                    val familyIdx = families.indexOf(family)
                    Swatch(
                        color = rep,
                        size = familySwatchSize,
                        selected = familyIdx == activeFamily,
                        accent = colors.accent,
                        onClick = {
                            activeFamily = familyIdx
                            state.setFromColor(rep)
                        },
                    )
                }
                // Pad the last (possibly short) row so swatches stay left-anchored
                repeat(columnsPerRow - rowFamilies.size) {
                    Spacer(Modifier.size(familySwatchSize))
                }
            }
        }

        // Shade row for the active family
        AnimatedVisibility(
            visible = activeFamily in families.indices,
            enter = fadeIn() + expandVertically(),
            exit  = fadeOut() + shrinkVertically(),
        ) {
            Column {
                Spacer(Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surfaceInset),
                ) {
                    Column(modifier = modifier.fillMaxWidth().padding(12.dp)) {
                        // Family grid — chunked into rows of [columnsPerRow]
                        families[activeFamily].chunked(columnsPerRow).forEachIndexed { rowIndex, rowFamilies ->
                            if (rowIndex > 0) Spacer(Modifier.height(10.dp))
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                rowFamilies.forEach { shade ->
                                    Swatch(
                                        color = shade,
                                        size = shadeSwatchSize,
                                        selected = shade == current,
                                        accent = colors.accent,
                                        onClick = {
                                            state.setFromColor(shade)
                                        },
                                    )
                                }
                                // Pad the last (possibly short) row so swatches stay left-anchored
                                repeat(columnsPerRow - rowFamilies.size) {
                                    Spacer(Modifier.size(familySwatchSize))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Filled circle with an optional accent ring when [selected]. */
@Composable
private fun Swatch(
    color: Color,
    size: Dp,
    selected: Boolean,
    accent: Color,
    onClick: () -> Unit,
) {
    val ringThickness = 2.5.dp
    val ringGap = 2.dp
    Box(
        modifier = Modifier
            .size(size)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            // Outer accent ring
            Box(
                Modifier
                    .size(size)
                    .clip(CircleShape)
                    .border(ringThickness, accent, CircleShape),
            )
        }
        // Color circle, slightly inset when selected so the ring breathes
        val swatchSize = if (selected) size - (ringThickness + ringGap) * 2 else size
        Box(
            Modifier
                .size(swatchSize)
                .clip(CircleShape)
                .background(color),
        )
    }
}

/** Find the index of the first family containing a color equal to [c]; -1 if none. */
private fun List<List<Color>>.indexOfFirstFamily(c: Color): Int =
    indexOfFirst { family -> family.any { it == c } }
