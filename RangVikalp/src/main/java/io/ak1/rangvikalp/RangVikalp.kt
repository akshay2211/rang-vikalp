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