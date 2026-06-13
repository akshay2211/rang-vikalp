/*
 * Copyright (C) 2026 akshay2211 (Akshay Sharma)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package io.ak1.rangvikalp

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/** Hollow white circle thumb with a soft dark halo — shared by SV box & sliders. */
internal fun DrawScope.drawPickerThumb(center: Offset, radius: Float) {
    drawCircle(
        color  = Color.Black.copy(alpha = 0.25f),
        radius = radius + 1.dp.toPx(),
        center = center,
        style  = Stroke(width = 3.dp.toPx()),
    )
    drawCircle(
        color  = Color.White,
        radius = radius,
        center = center,
        style  = Stroke(width = 2.dp.toPx()),
    )
}