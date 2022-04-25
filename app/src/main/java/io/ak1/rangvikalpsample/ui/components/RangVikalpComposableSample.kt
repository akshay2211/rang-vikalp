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

package io.ak1.rangvikalpsample.ui.components

import android.content.pm.ActivityInfo
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding
import io.ak1.rangvikalp.RangVikalp
import io.ak1.rangvikalp.defaultSelectedColor
import io.ak1.rangvikalpsample.R
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * Created by akshay on 16/04/22
 * https://ak1.io
 */

@Composable
fun RangVikalpComposable(themeCallback: (Boolean) -> Unit) {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    val defaultColor = defaultSelectedColor
    var color = remember { Animatable(defaultColor) }
    val coroutine = rememberCoroutineScope()
    var isVisible by remember { mutableStateOf(false) }
    var isShadesVisible by remember { mutableStateOf(false) }
    var isLight by remember { mutableStateOf(true) }
    val floatAnimateAsState by animateFloatAsState(targetValue = if (isVisible) 180f else 0f)

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(color.value)
                    .statusBarsPadding()
            ) {
                TopAppBar(
                    backgroundColor = Color.Transparent,
                    elevation = 0.dp,

                    ) {
                    Text(
                        text = "RangVikalp", modifier = Modifier.padding(16.dp, 0.dp)
                    )
                }
            }

        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colors.background
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = { isVisible = !isVisible }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_chevron_up),
                                contentDescription = "",
                                tint = color.value,
                                modifier = Modifier
                                    .padding(12.dp)
                                    .rotate(floatAnimateAsState)
                            )
                        }
                    }
                    RangVikalp(
                        isVisible = isVisible,
                        showShades = isShadesVisible
                    ) {
                        coroutine.launch {
                            async { color.animateTo(it, animationSpec = tween(1000)) }
                        }
                    }
                }
            }

        }) {

        val modifier = Modifier.fillMaxWidth().padding(0.dp, 6.dp)
        val roundedCornerShape = RoundedCornerShape(8.dp)

        LazyColumn(Modifier.padding(16.dp, 0.dp)) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            items(
                data(
                    isVisible,
                    isShadesVisible,
                    isLight,
                    { isVisible = !isVisible },
                    { isShadesVisible = !isShadesVisible }) {
                    isLight = !isLight
                    themeCallback(isLight)
                }) {
                Row(
                    modifier = modifier,
                    horizontalArrangement = it.horizontalArrangement,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (it.horizontalArrangement == Arrangement.End) {
                        Spacer(modifier = Modifier.width(70.dp))
                    }
                    Surface(
                        color = color.value,
                        shape = roundedCornerShape,
                    ) {
                        ClickableText(
                            text = textFormatter(it.text),
                            style = MaterialTheme.typography.body2.copy(color = LocalContentColor.current),
                            modifier = Modifier.padding(8.dp), onClick = it.onClick
                        )
                    }
                }
            }
        }
    }
}


