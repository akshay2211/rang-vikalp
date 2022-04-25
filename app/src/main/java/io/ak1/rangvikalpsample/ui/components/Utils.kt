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

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

/**
 * Created by akshay on 16/04/22
 * https://ak1.io
 */


@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            // restore original orientation when view disappears
            activity.requestedOrientation = originalOrientation
        }
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

data class Texts(
    val text: String,
    val horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    val onClick: (Int) -> Unit
)

fun data(
    isVisible: Boolean,
    isShadesVisible: Boolean,
    isDarkTheme: Boolean,
    showHide: (Int) -> Unit,
    shadesView: (Int) -> Unit,
    theme: (Int) -> Unit
): MutableList<Texts> = mutableListOf<Texts>().apply {
    add(
        Texts(
            "Hello developers,\nThis is sample of RangVikalp - a minimal color selection Library.",
            Arrangement.End
        ) {})
    add(Texts("Nice!!,\nTell me more...", Arrangement.Start) {})
    add(
        Texts(
            "Click here to ${if (isVisible) "hide" else "show"} RangVikalp, and try selecting different colors.",
            Arrangement.End,
            showHide
        )
    )
    add(Texts("Can i choose a specific shade too,\nfor any color?", Arrangement.Start) {})
    add(
        Texts(
            "Click here to ${if (isShadesVisible) "hide" else "show"} shades",
            Arrangement.End,
            shadesView
        )
    )
    add(
        Texts(
            "Can i see this in a ${if (!isDarkTheme) "Dark" else "Light"} theme?",
            Arrangement.Start
        ) {})
    add(Texts("Click here to switch to change theme", Arrangement.End, theme))
}


@Composable
fun textFormatter(text: String) = buildAnnotatedString {
    if (text.contains("Click here", true)) {
        append(
            AnnotatedString(
                text = text.slice(0 until 10),
                spanStyle = SpanStyle(
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif
                )
            )
        )
        append(
            AnnotatedString(
                text = text.slice(10 until text.length),
                spanStyle = SpanStyle(fontStyle = FontStyle.Italic, fontWeight = FontWeight.Light)
            )
        )
    } else {
        append(text)
    }

}