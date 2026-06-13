/*
 * Copyright (C) 2026 akshay2211 (Akshay Sharma)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package io.ak1.rangvikalp

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Pill-style tab selector. The selected tab is drawn as an animated rounded
 * pill behind the active label; inactive labels are muted.
 *
 * Used inside [RangVikalp] to flip between the Preset & Custom views but
 * exposed publicly so consumers can drop it into their own layouts.
 */
@Composable
fun TabStrip(
    tabs: List<String>,
    selectedIndex: Int,
    colors: RangVikalpColors,
    modifier: Modifier = Modifier,
    height: Dp = 44.dp,
    onSelect: (Int) -> Unit,
) {
    if (tabs.isEmpty()) return
    val safeIndex = selectedIndex.coerceIn(0, tabs.lastIndex)
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(14.dp))
            .background(colors.surfaceInset)
            .padding(4.dp),
    ) {
        val pillWidth = maxWidth / tabs.size
        val pillOffset by animateDpAsState(targetValue = pillWidth * safeIndex)

        // Animated selection pill
        Box(
            Modifier
                .offset(x = pillOffset)
                .fillMaxHeight()
                .width(pillWidth)
                .clip(RoundedCornerShape(10.dp))
                .background(colors.surface),
        )

        // Labels — sit above the pill, tap-targets sized 1/N of the row
        Row(Modifier.fillMaxWidth().fillMaxHeight()) {
            tabs.forEachIndexed { i, label ->
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onSelect(i) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        color = if (i == safeIndex) colors.onSurface else colors.onSurfaceMuted,
                        fontSize = 15.sp,
                        fontWeight = if (i == safeIndex) FontWeight.SemiBold else FontWeight.Medium,
                    )
                }
            }
        }
    }
}

