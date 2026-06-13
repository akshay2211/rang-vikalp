/*
 * Copyright (C) 2026 akshay2211 (Akshay Sharma)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package io.ak1.rangvikalp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Rainbow hue slider. Drag/tap anywhere along the bar; writes to
 * [RangVikalpState.hue] (0..360). Exposed publicly so consumers can compose just
 * the hue slider on its own.
 */
@Composable
fun HueSlider(
    state: RangVikalpState,
    modifier: Modifier = Modifier,
    trackHeight: Dp = 12.dp,
    thumbRadius: Dp = 10.dp,
) {
    // Sampled across the spectrum so the gradient closes cleanly at 360 → 0 (red).
    val hueColors = remember {
        listOf(
            Color.hsv(0f,   1f, 1f),
            Color.hsv(60f,  1f, 1f),
            Color.hsv(120f, 1f, 1f),
            Color.hsv(180f, 1f, 1f),
            Color.hsv(240f, 1f, 1f),
            Color.hsv(300f, 1f, 1f),
            Color.hsv(360f, 1f, 1f),
        )
    }
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(thumbRadius * 2 + 4.dp)
            .pointerInput(state) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    state.setHueFromX(down.position.x, size.width)
                    down.consume()
                    drag(down.id) { change ->
                        state.setHueFromX(change.position.x, size.width)
                        change.consume()
                    }
                }
            },
    ) {
        val trackPx  = trackHeight.toPx()
        val trackTop = (size.height - trackPx) / 2f
        drawRoundRect(
            brush        = Brush.horizontalGradient(hueColors),
            topLeft      = Offset(0f, trackTop),
            size         = Size(size.width, trackPx),
            cornerRadius = CornerRadius(trackPx / 2f),
        )
        val r   = thumbRadius.toPx()
        val pad = r + 1.dp.toPx()
        val cx  = ((state.hue / 360f) * size.width).coerceIn(pad, size.width - pad)
        val cy  = size.height / 2f
        drawPickerThumb(Offset(cx, cy), r)
    }
}

private fun RangVikalpState.setHueFromX(x: Float, width: Int) {
    if (width <= 0) return
    hue = (x / width.toFloat()).coerceIn(0f, 1f) * 360f
}