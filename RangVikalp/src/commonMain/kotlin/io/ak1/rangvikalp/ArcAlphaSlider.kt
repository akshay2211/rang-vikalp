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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * Arc-shaped alpha picker — the curved cousin of [AlphaSlider]. The full
 * 0..1 alpha range maps along an arc of [sweepDeg] degrees rotated by
 * [rotationDeg]. The transparent end fades into [transparentBacking] (a
 * solid color drawn behind the gradient) so the slider reads clearly even
 * when the host's surface is dark or busy.
 *
 * Default sweep is `300°` — a ring with a small dead zone at the bottom — so
 * the transparent start and opaque end land at visually distinct positions.
 * Set `sweepDeg = 360f` if you want a closed ring.
 *
 * @param rotationDeg       canvas angle where the arc begins (default: top)
 * @param sweepDeg          arc span; defaults to 300° (3/4 + a wedge)
 * @param trackThickness    stroke thickness of the arc track
 * @param thumbRadius       size of the draggable thumb riding the arc
 * @param transparentBacking solid color painted under the gradient so the
 *                          transparent end is visible against any background
 * @param segmentCount      arc-segment count for the gradient (perf knob)
 */
@Composable
fun ArcAlphaSlider(
    state: RangVikalpState,
    modifier: Modifier = Modifier,
    rotationDeg: Float = -90f,
    sweepDeg: Float = 300f,
    trackThickness: Dp = 14.dp,
    thumbRadius: Dp = 10.dp,
    transparentBacking: Color = Color(0xFFC9C9C9),
    segmentCount: Int = 96,
) {
    Canvas(
        modifier = modifier.pointerInput(state, rotationDeg, sweepDeg) {
            awaitEachGesture {
                val down = awaitFirstDown()
                state.alpha = arcFraction(down.position, rotationDeg, sweepDeg)
                down.consume()
                drag(down.id) { change ->
                    state.alpha = arcFraction(change.position, rotationDeg, sweepDeg)
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

        // 1. Solid backing along the whole arc so the transparent end
        //    reads against a known color rather than whatever's behind us.
        drawArc(
            color      = transparentBacking,
            startAngle = rotationDeg,
            sweepAngle = sweepDeg,
            useCenter  = false,
            topLeft    = arcTopLeft,
            size       = arcSize,
            style      = Stroke(width = trackPx, cap = StrokeCap.Round),
        )

        // 2. Transparent → opaque hue gradient as many tiny segments.
        val hue       = state.pureHueColor
        val segSweep  = sweepDeg / segmentCount + 0.5f
        for (i in 0 until segmentCount) {
            val frac = i.toFloat() / segmentCount
            drawArc(
                color      = hue.copy(alpha = frac),
                startAngle = rotationDeg + frac * sweepDeg,
                sweepAngle = segSweep,
                useCenter  = false,
                topLeft    = arcTopLeft,
                size       = arcSize,
                style      = Stroke(width = trackPx, cap = StrokeCap.Butt),
            )
        }

        // Thumb
        val fraction = state.alpha.coerceIn(0f, 1f)
        val angleRad = ((rotationDeg + fraction * sweepDeg) * DegToRad)
        drawPickerThumb(
            center = Offset(cx + ringR * cos(angleRad), cy + ringR * sin(angleRad)),
            radius = thumbPx,
        )
    }
}
