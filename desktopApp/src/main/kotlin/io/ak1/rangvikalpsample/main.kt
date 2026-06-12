package io.ak1.rangvikalpsample

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "RangVikalpSample",
    ) {
        App()
    }
}