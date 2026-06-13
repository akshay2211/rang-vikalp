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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.sqrt

/**
 * Saturation/Value picker box.
 *
 *  X axis ── saturation 0 → 1   (white  → pure hue)
 *  Y axis ── value      1 → 0   (transparent → black overlay)
 *
 * Tap or drag anywhere inside the box to move the thumb. Reads hue from
 * [state] and writes [state.saturation] + [state.value] back.
 *
 * Exposed publicly so consumers can re-use just the SV box outside the full
 * [RangVikalp] composition.
 */
@Composable
fun SaturationValueBox(
    state: RangVikalpState,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    thumbRadius: Dp = 11.dp,
) {
    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .pointerInput(state) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    state.updateSV(down.position, size.width, size.height)
                    down.consume()
                    drag(down.id) { change ->
                        state.updateSV(change.position, size.width, size.height)
                        change.consume()
                    }
                }
            },
    ) {
        // Layer 1 — saturation gradient (white → pure hue)
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(Color.White, state.pureHueColor),
            ),
        )
        // Layer 2 — value gradient (transparent → black) painted on top
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color.Black),
            ),
        )
        // Thumb position — inset so the ring stays fully inside the rounded
        // box even at the curved corners (saturation/value still reach 0 / 1).
        val r        = thumbRadius.toPx()
        val cornerPx = cornerRadius.toPx()
        val rawX     = state.saturation * size.width
        val rawY     = (1f - state.value) * size.height
        val clamped = clampInsideRoundedRect(
            x = rawX, y = rawY,
            width = size.width, height = size.height,
            cornerRadius = cornerPx,
            thumbRadius = r + 1.dp.toPx(),
        )
        drawPickerThumb(clamped, r)
    }
}

private fun RangVikalpState.updateSV(position: Offset, width: Int, height: Int) {
    if (width <= 0 || height <= 0) return
    saturation = (position.x / width.toFloat()).coerceIn(0f, 1f)
    value      = (1f - position.y / height.toFloat()).coerceIn(0f, 1f)
}

/**
 * Constrains a point so that a circle of [thumbRadius] drawn at it lies
 * fully inside a [width] × [height] rounded rectangle with [cornerRadius].
 *
 * Straight edges → linear inset; curved corners → push the point onto the
 * arc concentric with the corner so the thumb tracks the rounded edge.
 */
private fun clampInsideRoundedRect(
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    cornerRadius: Float,
    thumbRadius: Float,
): Offset {
    val r  = thumbRadius
    val cr = cornerRadius
    var cx = x.coerceIn(r, width  - r)
    var cy = y.coerceIn(r, height - r)
    val maxCornerDist = cr - r
    if (maxCornerDist <= 0f) return Offset(cx, cy)

    // Identify which (if any) corner quadrant the point is in, then project
    // onto the inner arc.
    val inLeft   = cx < cr
    val inRight  = cx > width  - cr
    val inTop    = cy < cr
    val inBottom = cy > height - cr
    val ccx: Float
    val ccy: Float
    when {
        inLeft  && inTop    -> { ccx = cr;            ccy = cr             }
        inRight && inTop    -> { ccx = width - cr;    ccy = cr             }
        inLeft  && inBottom -> { ccx = cr;            ccy = height - cr    }
        inRight && inBottom -> { ccx = width - cr;    ccy = height - cr    }
        else                -> return Offset(cx, cy) // on a straight edge
    }
    val dx = cx - ccx
    val dy = cy - ccy
    val d  = sqrt(dx * dx + dy * dy)
    if (d > maxCornerDist) {
        val s = maxCornerDist / d
        cx = ccx + dx * s
        cy = ccy + dy * s
    }
    return Offset(cx, cy)
}