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
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun RangVikalp(
    isVisible: Boolean,
    rowElementsCount: Int = 8,
    showShades: Boolean = true,
    colorIntensity: Int = 5,
    unSelectedSize: Dp = 26.dp,
    selectedSize: Dp = 36.dp,
    colors: List<List<Color>> = colorArray,
    clickedColor: (Color) -> Unit
) {
    val colorIntensity = if (colorIntensity in 10..-1) 5 else colorIntensity
    val density = LocalDensity.current
    var defaultColor by remember {
        mutableStateOf(colors[0][colorIntensity])
    }
    var defaultRow by remember {
        mutableStateOf(colors[0])
    }
    var subColorsRowVisibility by remember {
        mutableStateOf(true)
    }

    ChangeVisibility(isVisible, density) {
        val parentList = colors.chunked(rowElementsCount)
        Column(modifier = Modifier.padding(16.dp, 0.dp)) {

            if (showShades) {
                ChangeVisibility(
                    subColorsRowVisibility,
                    density
                ) {
                    val parentList = defaultRow.chunked(rowElementsCount)

                    Column {

                        parentList.forEachIndexed { _, colorRow ->
                            SubColorRow(
                                rowElementsCount = rowElementsCount,
                                colorRow = colorRow,
                                defaultColor = defaultColor,
                                unSelectedSize = unSelectedSize,
                                selectedSize = selectedSize
                            ) {
                                defaultColor = it
                                clickedColor(it)
                            }
                        }
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp)
                        )
                    }

                }
            }
            parentList.forEachIndexed { _, colorRow ->
                ColorRow(
                    rowElementsCount = rowElementsCount,
                    colorRow = colorRow,
                    colorIntensity = colorIntensity,
                    defaultColor = defaultColor,
                    unSelectedSize = unSelectedSize,
                    selectedSize = selectedSize
                ) { colorRow, color ->
                    subColorsRowVisibility =
                        subColorsRowVisibility == false || defaultColor.value != color.value
                    defaultColor = color
                    defaultRow = colorRow
                    if (subColorsRowVisibility)
                        clickedColor(color)
                }

            }

        }
    }
}

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