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
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.ceil

/**
 * Alpha (opacity) slider. Background is a 2-row checkerboard so partial
 * transparency reads against a known pattern, with a transparent → opaque
 * gradient of the current pure-hue color painted on top. Writes [RangVikalpState.alpha].
 */
@Composable
fun AlphaSlider(
    state: RangVikalpState,
    modifier: Modifier = Modifier,
    trackHeight: Dp = 12.dp,
    thumbRadius: Dp = 10.dp,
    checkerLight: Color = Color.White,
    checkerDark: Color = Color(0xFFC9C9C9),
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(thumbRadius * 2 + 4.dp)
            .pointerInput(state) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    state.setAlphaFromX(down.position.x, size.width)
                    down.consume()
                    drag(down.id) { change ->
                        state.setAlphaFromX(change.position.x, size.width)
                        change.consume()
                    }
                }
            },
    ) {
        val trackPx  = trackHeight.toPx()
        val trackTop = (size.height - trackPx) / 2f
        val cornerPx = trackPx / 2f
        val trackRect = Rect(0f, trackTop, size.width, trackTop + trackPx)
        val clip = Path().apply { addRoundRect(RoundRect(trackRect, CornerRadius(cornerPx))) }

        clipPath(clip) {
            // Checker base
            drawRect(
                color   = checkerLight,
                topLeft = Offset(0f, trackTop),
                size    = Size(size.width, trackPx),
            )
            val square = trackPx / 2f
            val cols   = ceil(size.width / square).toInt()
            for (row in 0..1) {
                for (col in 0 until cols) {
                    if ((row + col) % 2 == 0) {
                        drawRect(
                            color   = checkerDark,
                            topLeft = Offset(col * square, trackTop + row * square),
                            size    = Size(square, square),
                        )
                    }
                }
            }
            // Transparent → opaque hue gradient
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        state.pureHueColor.copy(alpha = 0f),
                        state.pureHueColor.copy(alpha = 1f),
                    ),
                ),
                topLeft = Offset(0f, trackTop),
                size    = Size(size.width, trackPx),
            )
        }

        val r   = thumbRadius.toPx()
        val pad = r + 1.dp.toPx()
        val cx  = (state.alpha * size.width).coerceIn(pad, size.width - pad)
        val cy  = size.height / 2f
        drawPickerThumb(Offset(cx, cy), r)
    }
}

private fun RangVikalpState.setAlphaFromX(x: Float, width: Int) {
    if (width <= 0) return
    alpha = (x / width.toFloat()).coerceIn(0f, 1f)
}