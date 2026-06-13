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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * Arc-shaped hue picker — the curved cousin of [HueSlider]. The full hue
 * spectrum is mapped along an arc of [sweepDeg] degrees, rotated to start at
 * [rotationDeg] (canvas convention: 0° points right, +90° points down — so
 * the default `-90°` puts the start of the arc at the top).
 *
 * Useful as a ring wrapped around [SaturationValueCircle], or as a half/arc
 * indicator at the top of a layout.
 *
 * @param rotationDeg   canvas angle where the arc begins (default: top)
 * @param sweepDeg      arc span; default 360° = full ring. Values < 360°
 *                      leave a dead zone where pointer presses snap to the
 *                      nearest endpoint.
 * @param trackThickness stroke thickness of the arc track
 * @param thumbRadius   size of the draggable thumb riding the arc
 * @param segmentCount  how many tiny arc segments compose the gradient.
 *                      Higher = smoother, lower = cheaper. 96 is the sweet
 *                      spot for typical sizes.
 */
@Composable
fun ArcHueSlider(
    state: RangVikalpState,
    modifier: Modifier = Modifier,
    rotationDeg: Float = -90f,
    sweepDeg: Float = 360f,
    trackThickness: Dp = 14.dp,
    thumbRadius: Dp = 10.dp,
    segmentCount: Int = 96,
) {
    Canvas(
        modifier = modifier.pointerInput(state, rotationDeg, sweepDeg) {
            awaitEachGesture {
                val down = awaitFirstDown()
                state.hue = arcFraction(down.position, rotationDeg, sweepDeg) * 360f
                down.consume()
                drag(down.id) { change ->
                    state.hue = arcFraction(change.position, rotationDeg, sweepDeg) * 360f
                    change.consume()
                }
            }
        },
    ) {
        val trackPx = trackThickness.toPx()
        val thumbPx = thumbRadius.toPx()
        val cx      = size.width  / 2f
        val cy      = size.height / 2f
        val ringR   = min(cx, cy) - max(trackPx / 2f, thumbPx) - 1.dp.toPx()
        val arcTopLeft = Offset(cx - ringR, cy - ringR)
        val arcSize    = Size(ringR * 2f, ringR * 2f)

        // Gradient by way of many tiny solid-color arc segments — keeps the
        // mapping exact for any sweepDeg, where Brush.sweepGradient would
        // either need elaborate colorstops or always span 360°.
        val segSweep = sweepDeg / segmentCount + 0.5f  // hairline overlap hides aa-gaps
        for (i in 0 until segmentCount) {
            val frac = i.toFloat() / segmentCount
            drawArc(
                color      = Color.hsv(frac * 360f, 1f, 1f),
                startAngle = rotationDeg + frac * sweepDeg,
                sweepAngle = segSweep,
                useCenter  = false,
                topLeft    = arcTopLeft,
                size       = arcSize,
                style      = Stroke(width = trackPx, cap = StrokeCap.Butt),
            )
        }
        // Rounded end-caps for a cleaner finish when sweep < 360°
        if (sweepDeg < 359.5f) {
            drawArc(
                color = Color.hsv(0f, 1f, 1f),
                startAngle = rotationDeg,
                sweepAngle = 0.1f,
                useCenter = false,
                topLeft = arcTopLeft,
                size = arcSize,
                style = Stroke(width = trackPx, cap = StrokeCap.Round),
            )
            drawArc(
                color = Color.hsv(360f, 1f, 1f),
                startAngle = rotationDeg + sweepDeg - 0.1f,
                sweepAngle = 0.1f,
                useCenter = false,
                topLeft = arcTopLeft,
                size = arcSize,
                style = Stroke(width = trackPx, cap = StrokeCap.Round),
            )
        }

        // Thumb
        val fraction = (state.hue / 360f).coerceIn(0f, 1f)
        val angleRad = ((rotationDeg + fraction * sweepDeg) * DegToRad)
        drawPickerThumb(
            center = Offset(cx + ringR * cos(angleRad), cy + ringR * sin(angleRad)),
            radius = thumbPx,
        )
    }
}

/* ──────────────────────────────────────────────────────────────────────────
 *  Shared arc-touch math — also used by ArcAlphaSlider
 * ────────────────────────────────────────────────────────────────────────── */

internal const val DegToRad = kotlin.math.PI.toFloat() / 180f

/**
 * Convert a pointer position (relative to the Canvas top-left) into a fraction
 * `[0, 1]` along an arc rotated by [rotationDeg] and spanning [sweepDeg].
 *
 * Presses inside the arc's dead zone (when `sweepDeg < 360`) snap to whichever
 * endpoint they're closer to, so the slider never sits in an invalid state.
 */
internal fun PointerInputScope.arcFraction(
    pos: Offset,
    rotationDeg: Float,
    sweepDeg: Float,
): Float {
    val cx = size.width  / 2f
    val cy = size.height / 2f
    val dx = pos.x - cx
    val dy = pos.y - cy
    // atan2 → degrees, normalize into [0, 360) relative to the arc start.
    var angle = atan2(dy, dx) / DegToRad
    angle = ((angle - rotationDeg) % 360f + 360f) % 360f
    if (angle <= sweepDeg) return angle / sweepDeg
    // In the dead zone — snap to the nearer endpoint.
    val distToEnd   = angle - sweepDeg
    val distToStart = 360f - angle
    return if (distToStart < distToEnd) 0f else 1f
}
