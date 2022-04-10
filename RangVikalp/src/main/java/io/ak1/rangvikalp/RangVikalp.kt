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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.ak1.rangvikalp.custompicker.HueRadialPicker

@Composable
fun RangVikalp(
    isVisible: Boolean,
    rowElementsCount: Int = 8,
    colors: List<Color>,
    clickedColor: (Color) -> Unit
) {
    val density = LocalDensity.current
    val defaultColor = remember {
        mutableStateOf(colors[0])
    }

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
        ) + fadeOut()
    ) {
        val parentList = colors.chunked(rowElementsCount)

        Column(modifier = Modifier.padding(16.dp, 0.dp)) {
            parentList.forEachIndexed { _, colorRow ->
                Row {
                    repeat(rowElementsCount) { rowIndex ->
                        if (colorRow.size - 1 < rowIndex) {
                            Spacer(Modifier.weight(1f, true))
                            return@repeat
                        }
                        val color = colorRow[rowIndex]
                        ColorDots(
                            color,
                            color == defaultColor.value,
                            24.dp,
                            36.dp,
                        ) {
                            defaultColor.value = it
                            clickedColor(color)
                        }
                    }
                }
            }
        }
    }
}


@Composable
internal fun RowScope.ColorDots(
    color: Color,
    selected: Boolean,
    defaultSize: Dp,
    expandedSize: Dp,
    dotDescription: String = stringResource(id = R.string.color_dot),
    clickedColor: (Color) -> Unit
) {
    val dbAnimateAsState: Dp by animateDpAsState(
        targetValue = if (selected) expandedSize else defaultSize
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