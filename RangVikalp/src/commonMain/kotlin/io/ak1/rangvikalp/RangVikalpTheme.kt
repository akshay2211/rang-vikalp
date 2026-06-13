/*
 * Copyright (C) 2026 akshay2211 (Akshay Sharma)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package io.ak1.rangvikalp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Theming surface for the [RangVikalp] picker chrome. Defaults are tuned to
 * match the reference designs (deep-violet dark mode, near-white light mode).
 */
@Immutable
data class RangVikalpColors(
    /** Outer card background. */
    val surface: Color,
    /** Backgrounds for inset tiles (HEX field, eyedropper button, sliders). */
    val surfaceInset: Color,
    /** Hairline border for card + tiles. */
    val border: Color,
    /** Primary foreground (text, icons). */
    val onSurface: Color,
    /** Muted foreground (secondary labels, inactive chevrons). */
    val onSurfaceMuted: Color,
    /** Selection accent — drives the ring around the picked preset swatch. */
    val accent: Color,
)

@Composable
fun defaultRangVikalpColors(dark: Boolean = isSystemInDarkTheme()): RangVikalpColors =
    if (dark) RangVikalpColors(
        surface         = Color(0xFF0F0A2D),
        surfaceInset    = Color(0xFF1A1547),
        border          = Color(0xFF2A2657),
        onSurface       = Color(0xFFE8E8F5),
        onSurfaceMuted  = Color(0xFF8E8AB8),
        accent          = Color(0xFF7B5BFF),
    ) else RangVikalpColors(
        surface         = Color(0xFFFFFFFF),
        surfaceInset    = Color(0xFFFAFAFC),
        border          = Color(0xFFE5E5EA),
        onSurface       = Color(0xFF1A1A2E),
        onSurfaceMuted  = Color(0xFF8E8E93),
        accent          = Color(0xFF6E5BFF),
    )

/**
 * The default 9-swatch preset row from the reference, sourced from the
 * Material palette already shipped in [Colors.kt]. Override with any list.
 */
val defaultRangVikalpPresets: List<Color> = listOf(
    orange[4],      // FFA726 — warm orange
    amber[4],       // FFCA28 — yellow
    green[4],       // 66BB6A — green
    lightBlue[4],   // 29B6F6 — light blue
    blue[5],        // 2196F3 — blue (selected in references — index 4)
    indigo[4],      // 5C6BC0 — indigo
    deepPurple[3],  // 9575CD — purple
    pink[4],        // EC407A — pink
    red[3],         // E57373 — coral
)