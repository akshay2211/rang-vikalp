/*
 * Copyright (C) 2026 akshay2211 (Akshay Sharma)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package io.ak1.rangvikalp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Hoisted state for [RangVikalp].
 *
 * Internally we store HSV-A rather than RGB-A so the hue slider doesn't
 * jump when the user drags the SV thumb into the white/black corners
 * (RGB→HSV is lossy for achromatic colors — hue becomes undefined).
 */
@Stable
class RangVikalpState internal constructor(
    initialHue: Float,
    initialSaturation: Float,
    initialValue: Float,
    initialAlpha: Float,
) {
    var hue: Float by mutableFloatStateOf(initialHue.coerceIn(0f, 360f))
        internal set
    var saturation: Float by mutableFloatStateOf(initialSaturation.coerceIn(0f, 1f))
        internal set
    var value: Float by mutableFloatStateOf(initialValue.coerceIn(0f, 1f))
        internal set
    var alpha: Float by mutableFloatStateOf(initialAlpha.coerceIn(0f, 1f))
        internal set

    /** Currently picked color. */
    val color: Color
        get() = Color.hsv(hue, saturation, value, alpha)

    /** Pure hue at full S/V — drives the SV-box right edge and alpha gradient. */
    val pureHueColor: Color
        get() = Color.hsv(hue, 1f, 1f, 1f)

    /** 6-char uppercase hex of the current color, ignoring alpha. */
    val hex6: String
        get() = color.copy(alpha = 1f).toHex6()

    /** Alpha rendered as a 0..100 percent. */
    val opacityPercent: Int
        get() = (alpha * 100f).roundToInt()

    /**
     * Replace the current color. Preserves the existing hue when [c] is
     * achromatic so the hue slider stays where the user left it.
     */
    fun setFromColor(c: Color) {
        val (h, s, v) = c.toHsv()
        if (s > 0f) hue = h
        saturation = s
        value = v
        alpha = c.alpha
    }
}

@Composable
fun rememberRangVikalpState(initial: Color = defaultRangVikalpPresets[4]): RangVikalpState =
    remember {
        val (h, s, v) = initial.toHsv()
        RangVikalpState(h, s, v, initial.alpha)
    }

/* ──────────────────────────────────────────────────────────────────────────
 *  Color math
 * ────────────────────────────────────────────────────────────────────────── */

/** RGB → HSV. Returns (hue 0..360, saturation 0..1, value 0..1). */
internal fun Color.toHsv(): Triple<Float, Float, Float> {
    val r = red; val g = green; val b = blue
    val mx = max(r, max(g, b))
    val mn = min(r, min(g, b))
    val d  = mx - mn
    val h = when {
        d == 0f -> 0f
        mx == r -> 60f * (((g - b) / d) % 6f)
        mx == g -> 60f * (((b - r) / d) + 2f)
        else    -> 60f * (((r - g) / d) + 4f)
    }
    return Triple((h + 360f) % 360f, if (mx == 0f) 0f else d / mx, mx)
}

internal fun Color.toHex6(): String {
    val r = (red   * 255f).roundToInt().coerceIn(0, 255)
    val g = (green * 255f).roundToInt().coerceIn(0, 255)
    val b = (blue  * 255f).roundToInt().coerceIn(0, 255)
    return r.toHex2() + g.toHex2() + b.toHex2()
}

private fun Int.toHex2(): String {
    val table = "0123456789ABCDEF"
    return "${table[(this shr 4) and 0xF]}${table[this and 0xF]}"
}