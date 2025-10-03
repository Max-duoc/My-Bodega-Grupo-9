package com.example.mybodega_grupo9.ui.theme.Screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.mybodega_grupo9.ui.theme.utils.currentWindowWidthSize
import com.example.mybodega_grupo9.ui.theme.utils.WindowWidthSize

@Composable
fun AdaptiveHome(onAdd: () -> Unit) {
    when (currentWindowWidthSize()) {
        WindowWidthSize.Compact -> {
            HomeCompact(onAdd)
            Text("Vista Compacta") // Debug
        }
        WindowWidthSize.Medium -> {
            HomeMedium(onAdd)
            Text("Vista Mediana") // Debug
        }
        WindowWidthSize.Expanded -> {
            HomeExpanded(onAdd)
            Text("Vista Expandida") // Debug
        }
    }
}
