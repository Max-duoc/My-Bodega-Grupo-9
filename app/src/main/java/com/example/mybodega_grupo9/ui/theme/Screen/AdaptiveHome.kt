package com.example.mybodega_grupo9.ui.theme.Screen

import androidx.compose.runtime.Composable
import com.example.mybodega_grupo9.ui.theme.utils.currentWindowWidthSize
import com.example.mybodega_grupo9.ui.theme.utils.WindowWidthSize

@Composable
fun AdaptiveHome(onAdd: () -> Unit) {
    when (currentWindowWidthSize()) {
        WindowWidthSize.Compact -> HomeCompact(onAdd)
        WindowWidthSize.Medium -> HomeMedium(onAdd)
        WindowWidthSize.Expanded -> HomeExpanded(onAdd)
    }
}
