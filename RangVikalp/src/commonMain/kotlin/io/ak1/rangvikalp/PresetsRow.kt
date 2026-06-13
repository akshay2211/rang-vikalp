/*
 * Copyright (C) 2026 akshay2211 (Akshay Sharma)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package io.ak1.rangvikalp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.min

/**
 * Preset palette row: shuffle button + color swatches.
 *
 * - Tapping a swatch loads its color into [state].
 * - Tapping shuffle calls [onShuffle] to pick the next color; default skips
 *   the currently selected swatch so it always actually changes.
 *
 * Pieces are also public ([ShuffleButton], [PresetSwatch]) for custom layouts.
 */
@Composable
fun PresetsRow(
    state: RangVikalpState,
    presets: List<Color>,
    modifier: Modifier = Modifier,
    colors: RangVikalpColors = defaultRangVikalpColors(),
    swatchSize: Dp = 30.dp,
    onShuffle: () -> Color = {
        val current = state.color.copy(alpha = 1f)
        val pool = presets.filter { it != current }
        (pool.takeIf { it.isNotEmpty() } ?: presets).random()
    },
) {
    val current = state.color.copy(alpha = 1f)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth(),
    ) {
        ShuffleButton(
            colors = colors,
            size = swatchSize,
            onClick = { state.setFromColor(onShuffle()) },
        )
        presets.forEach { c ->
            PresetSwatch(
                color = c,
                selected = c == current,
                size = swatchSize,
                onClick = { state.setFromColor(c) },
            )
        }
    }
}

/** Filled color circle with an optional white checkmark when [selected]. */
@Composable
fun PresetSwatch(
    color: Color,
    selected: Boolean,
    size: Dp = 30.dp,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Canvas(Modifier.size(size * 0.55f)) { drawCheck(Color.White) }
        }
    }
}

/** Dashed-circle "randomize" affordance. */
@Composable
fun ShuffleButton(
    colors: RangVikalpColors,
    modifier: Modifier = Modifier,
    size: Dp = 30.dp,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(size * 0.78f)) { drawDashedCircle(colors.onSurfaceMuted) }
    }
}

/* ──────────────────────────────────────────────────────────────────────────
 *  Canvas icons
 * ────────────────────────────────────────────────────────────────────────── */

private fun DrawScope.drawCheck(color: Color) {
    val w = size.width
    val h = size.height
    val path = Path().apply {
        moveTo(w * 0.18f, h * 0.52f)
        lineTo(w * 0.42f, h * 0.76f)
        lineTo(w * 0.84f, h * 0.28f)
    }
    drawPath(
        path  = path,
        color = color,
        style = Stroke(
            width = 2.2.dp.toPx(),
            cap   = StrokeCap.Round,
            join  = StrokeJoin.Round,
        ),
    )
}

private fun DrawScope.drawDashedCircle(color: Color) {
    val radius = (min(size.width, size.height) / 2f) - 1.dp.toPx()
    val center = Offset(size.width / 2f, size.height / 2f)
    val path = Path().apply {
        addOval(Rect(center = center, radius = radius))
    }
    drawPath(
        path  = path,
        color = color,
        style = Stroke(
            width      = 1.5.dp.toPx(),
            cap        = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(2.5.dp.toPx(), 2.5.dp.toPx()),
            ),
        ),
    )
}