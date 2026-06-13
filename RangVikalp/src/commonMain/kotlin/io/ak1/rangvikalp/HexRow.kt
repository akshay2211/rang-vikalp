/*
 * Copyright (C) 2026 akshay2211 (Akshay Sharma)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package io.ak1.rangvikalp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * The HEX row — format chip · hex value with copy · opacity %.
 *
 * Each piece is also exposed as its own composable ([FormatChip],
 * [HexValueChip], [OpacityChip]) so consumers can lay them out differently.
 *
 * @param onCopy invoked after the hex string is copied to the clipboard, e.g.
 *               to show a snackbar. The clipboard write itself is handled here.
 */
@Composable
fun HexRow(
    state: RangVikalpState,
    modifier: Modifier = Modifier,
    colors: RangVikalpColors = defaultRangVikalpColors(),
    height: Dp = 48.dp,
    onCopy: ((String) -> Unit)? = null,
) {
    @Suppress("DEPRECATION") val clipboard = LocalClipboardManager.current
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth().height(height),
    ) {
       /* FormatChip(
            label = "HEX",
            colors = colors,
            modifier = Modifier.width(88.dp).fillMaxHeight(),
        )*/
        HexValueChip(
            value = state.hex6,
            colors = colors,
            modifier = Modifier.weight(1f).fillMaxHeight(),
            onCopy = {
                val toCopy = "#${state.hex6}"
                clipboard.setText(AnnotatedString(toCopy))
                onCopy?.invoke(toCopy)
            },
        )
        OpacityChip(
            percent = state.opacityPercent,
            colors = colors,
            modifier = Modifier.width(72.dp).fillMaxHeight(),
        )
    }
}

/* ──────────────────────────────────────────────────────────────────────────
 *  Pieces — public so they can be composed à la carte
 * ────────────────────────────────────────────────────────────────────────── */

@Composable
fun FormatChip(
    label: String,
    colors: RangVikalpColors,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    ChipTile(colors = colors, modifier = modifier, onClick = onClick) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text = label,
                color = colors.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(Modifier.size(6.dp))
            Canvas(Modifier.size(10.dp)) { drawChevronDown(colors.onSurfaceMuted) }
        }
    }
}

@Composable
fun HexValueChip(
    value: String,
    colors: RangVikalpColors,
    modifier: Modifier = Modifier,
    onCopy: () -> Unit,
) {
    ChipTile(colors = colors, modifier = modifier, onClick = onCopy) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
        ) {
            Text(
                text = value,
                color = colors.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
            )
            Canvas(Modifier.size(16.dp)) { drawCopyIcon(colors.onSurfaceMuted) }
        }
    }
}

@Composable
fun OpacityChip(
    percent: Int,
    colors: RangVikalpColors,
    modifier: Modifier = Modifier,
) {
    ChipTile(colors = colors, modifier = modifier) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "$percent%",
                color = colors.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

/* ──────────────────────────────────────────────────────────────────────────
 *  Internal — shared tile shell + canvas-drawn icons
 * ────────────────────────────────────────────────────────────────────────── */

@Composable
private fun ChipTile(
    colors: RangVikalpColors,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(14.dp)
    val base = Modifier
        .clip(shape)
        .background(colors.surfaceInset)
        .border(1.dp, colors.border, shape)
    Box(
        modifier = modifier
            .then(base)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
    ) { content() }
}

private fun DrawScope.drawChevronDown(color: Color) {
    val w = size.width
    val h = size.height
    val path = Path().apply {
        moveTo(w * 0.15f, h * 0.35f)
        lineTo(w * 0.50f, h * 0.70f)
        lineTo(w * 0.85f, h * 0.35f)
    }
    drawPath(
        path  = path,
        color = color,
        style = Stroke(
            width = 1.6.dp.toPx(),
            cap   = StrokeCap.Round,
            join  = StrokeJoin.Round,
        ),
    )
}

/** Two overlapping rounded squares — the standard "copy" glyph. */
private fun DrawScope.drawCopyIcon(color: Color) {
    val w = size.width
    val h = size.height
    val squareW = w * 0.68f
    val squareH = h * 0.68f
    val radius  = CornerRadius(w * 0.12f)
    val stroke  = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    // Back square — upper-right
    drawRoundRect(
        color        = color,
        topLeft      = Offset(w - squareW, 0f),
        size         = Size(squareW, squareH),
        cornerRadius = radius,
        style        = stroke,
    )
    // Front square — lower-left
    drawRoundRect(
        color        = color,
        topLeft      = Offset(0f, h - squareH),
        size         = Size(squareW, squareH),
        cornerRadius = radius,
        style        = stroke,
    )
}