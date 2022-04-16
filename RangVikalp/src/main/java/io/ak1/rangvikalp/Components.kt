/*
 * Copyright (C) 2022 akshay2211 (Akshay Sharma)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.ak1.rangvikalp

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Created by akshay on 16/04/22
 * https://ak1.io
 */

/**
 * horizontal stack to show color dots
 * */
@Composable
internal fun ColorRow(
    rowElementsCount: Int,
    colorRow: List<List<Color>>,
    colorIntensity: Int,
    defaultColor: Color,
    unSelectedSize: Dp,
    selectedSize: Dp,
    clickedColor: (List<Color>, Color) -> Unit
) {
    Row {
        repeat(rowElementsCount) { rowIndex ->
            if (colorRow.size - 1 < rowIndex) {
                Spacer(Modifier.weight(1f, true))
                return@repeat
            }
            val color = colorRow[rowIndex]
            ColorDots(
                color[colorIntensity],
                color.contains(defaultColor),
                unSelectedSize,
                selectedSize
            ) {
                clickedColor(color, it)
            }
        }
    }
}
/**
 * horizontal stack to show shades of a selected color
 * */
@Composable
internal fun SubColorRow(
    rowElementsCount: Int,
    colorRow: List<Color>,
    defaultColor: Color,
    unSelectedSize: Dp,
    selectedSize: Dp,
    clickedColor: (Color) -> Unit
) {
    Row {
        repeat(rowElementsCount) { rowIndex ->
            if (colorRow.size - 1 < rowIndex) {
                Spacer(Modifier.weight(1f, true))
                return@repeat
            }
            val color = colorRow[rowIndex]
            ColorDots(
                color,
                color == defaultColor,
                unSelectedSize,
                selectedSize, clickedColor = clickedColor
            )
        }
    }
}


/**
 * animated wrapper to manipulate visibility in a specific way
 * */
@Composable
internal fun ChangeVisibility(
    isVisible: Boolean,
    density: Density,
    content: @Composable() AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically {
            // Slide in from 40 dp from the top.
            with(density) {
                -40.dp.roundToPx()
            }
        } + expandVertically(
            // Expand from the top.
            expandFrom = Alignment.Top
        ) + fadeIn(
            // Fade in with the initial alpha of 0.3f.
            initialAlpha = 0.3f
        ),
        exit = slideOutVertically() + shrinkVertically(
            shrinkTowards = Alignment.Top
        ) + fadeOut(),
        content = content
    )
}
/**
 * Base entity to show a single color
 * */
@Composable
internal fun RowScope.ColorDots(
    color: Color,
    selected: Boolean,
    unSelectedSize: Dp = 26.dp,
    selectedSize: Dp = 36.dp,
    dotDescription: String = stringResource(id = R.string.color_dot),
    clickedColor: (Color) -> Unit
) {
    val dbAnimateAsState: Dp by animateDpAsState(
        targetValue = if (selected) selectedSize else unSelectedSize
    )
    IconButton(
        onClick = {
            clickedColor(color)
        }, modifier = Modifier
            .weight(1f, true)
    ) {
        Icon(
            painterResource(id = R.drawable.ic_color),
            contentDescription = dotDescription,
            tint = color,
            modifier = Modifier.size(dbAnimateAsState)
        )
    }
}