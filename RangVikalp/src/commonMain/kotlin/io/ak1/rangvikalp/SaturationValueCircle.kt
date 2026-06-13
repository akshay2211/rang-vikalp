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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Saturation/Value picker — circular variant of [SaturationValueBox].
 *
 *  X axis ── saturation 0 → 1   (white  → pure hue)
 *  Y axis ── value      1 → 0   (transparent → black overlay)
 *
 * The two SV gradients are drawn across the bounding rect and then clipped to
 * a disk. Drag input outside the disk is projected onto the circle boundary,
 * so the thumb always stays on the visible surface and the saturation / value
 * pair never points to a region the user can't see.
 *
 * Because of that disk-clipping the four square corners of (S, V) space are
 * unreachable — the picker covers only the inscribed circle of the SV plane.
 * Use [SaturationValueBox] when full SV coverage matters.
 */
@Composable
fun SaturationValueCircle(
    state: RangVikalpState,
    modifier: Modifier = Modifier,
    thumbRadius: Dp = 11.dp,
) {
    Canvas(
        modifier = modifier
            .clip(CircleShape)
            .pointerInput(state) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    state.updateSVOnDisk(down.position, size.width, size.height)
                    down.consume()
                    drag(down.id) { change ->
                        state.updateSVOnDisk(change.position, size.width, size.height)
                        change.consume()
                    }
                }
            },
    ) {
        // Layer 1 — saturation gradient (white → pure hue), painted across
        // the full rect; the CircleShape clip on the Canvas crops to a disk.
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(Color.White, state.pureHueColor),
            ),
        )
        // Layer 2 — value gradient (transparent → black)
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color.Black),
            ),
        )

        // Thumb — projected onto the inner disk so the ring stays fully
        // inside the visible circle even when (S, V) sit at an extreme.
        val r       = thumbRadius.toPx()
        val centerX = size.width  / 2f
        val centerY = size.height / 2f
        val maxR    = min(size.width, size.height) / 2f - r - 1.dp.toPx()
        val rawX    = state.saturation * size.width
        val rawY    = (1f - state.value) * size.height
        val dx      = rawX - centerX
        val dy      = rawY - centerY
        val d       = sqrt(dx * dx + dy * dy)
        val (cx, cy) = if (d > maxR && d > 0f) {
            val s = maxR / d
            centerX + dx * s to centerY + dy * s
        } else {
            rawX to rawY
        }
        drawPickerThumb(Offset(cx, cy), r)
    }
}

/**
 * Touch handler for the circular SV picker. Projects pointer positions that
 * fall outside the disk onto its boundary, then converts to (saturation,
 * value) the same way as the square box.
 */
private fun RangVikalpState.updateSVOnDisk(position: Offset, width: Int, height: Int) {
    if (width <= 0 || height <= 0) return
    val centerX = width  / 2f
    val centerY = height / 2f
    val maxR    = min(width, height) / 2f
    val dx      = position.x - centerX
    val dy      = position.y - centerY
    val d       = sqrt(dx * dx + dy * dy)
    val clampedX = if (d > maxR && d > 0f) centerX + dx * (maxR / d) else position.x
    val clampedY = if (d > maxR && d > 0f) centerY + dy * (maxR / d) else position.y
    saturation = (clampedX / width.toFloat()).coerceIn(0f, 1f)
    value      = (1f - clampedY / height.toFloat()).coerceIn(0f, 1f)
}
