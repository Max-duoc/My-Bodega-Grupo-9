package com.example.mybodega_grupo9.utils


import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

enum class WindowWidthSize { Compact, Medium, Expanded }

@Composable
fun currentWindowWidthSize(): WindowWidthSize {
    val config = LocalConfiguration.current
    val widthDp = config.screenWidthDp
    return when {
        widthDp < 600 -> WindowWidthSize.Compact
        widthDp < 840 -> WindowWidthSize.Medium
        else -> WindowWidthSize.Expanded
    }
}
