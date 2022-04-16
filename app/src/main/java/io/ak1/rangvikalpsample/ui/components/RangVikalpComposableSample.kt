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
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.statusBarsPadding
import io.ak1.rangvikalp.RangVikalp
import io.ak1.rangvikalp.defaultSelectedColor
import io.ak1.rangvikalpsample.R
import kotlinx.coroutines.launch

/**
 * Created by akshay on 16/04/22
 * https://ak1.io
 */

@Composable
fun RangVikalpComposable() {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    val defaultColor = defaultSelectedColor
    val color = remember { Animatable(defaultColor) }
    val coroutine = rememberCoroutineScope()
    var isVisible by remember {
        mutableStateOf(true)
    }
    var minimalView by remember {
        mutableStateOf(true)
    }
    val floatAnimateAsState by animateFloatAsState(
        targetValue = if (isVisible) 180f else 0f
    )
    Scaffold(modifier = Modifier.statusBarsPadding(),
        bottomBar = {
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
                    showShades = !minimalView
                ) {
                    coroutine.launch {
                        color.animateTo(it, animationSpec = tween(1000))
                    }
                }
            }

        }) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(6.dp)) {
            Text(text = "Minimal View", modifier = Modifier.padding(6.dp))
            Switch(checked = minimalView, onCheckedChange = {
                minimalView = !minimalView
            }, colors = SwitchDefaults.colors(checkedThumbColor = color.value))
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 200.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = { isVisible = !isVisible }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_color),
                    contentDescription = "",
                    tint = color.value,
                    modifier = Modifier
                        .size(200.dp)
                        .padding(4.dp)
                )
            }
        }

    }
}