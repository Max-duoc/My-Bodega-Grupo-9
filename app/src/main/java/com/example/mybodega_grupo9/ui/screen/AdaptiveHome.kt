package com.example.mybodega_grupo9.ui.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.mybodega_grupo9.ui.screen.HomeCompact
import com.example.mybodega_grupo9.ui.screen.HomeMedium
import com.example.mybodega_grupo9.ui.screen.HomeExpanded
import com.example.mybodega_grupo9.utils.rememberWindowSizeClass
import com.example.mybodega_grupo9.utils.WindowSizeClass

@Composable
fun AdaptiveHome(
    onNavigateToAdd: () -> Unit,
    onNavigateToDetails: () -> Unit
) {
    val windowInfo = rememberWindowSizeClass()

    when (windowInfo.widthSizeClass) {
        WindowSizeClass.Compact -> {
            HomeCompact(
                onNavigateToAdd = onNavigateToAdd,
                onNavigateToDetails = onNavigateToDetails
            )
            Text("Vista Compacta") // Debug opcional
        }

        WindowSizeClass.Medium -> {
            HomeMedium(
                onNavigateToAdd = onNavigateToAdd,
                onNavigateToDetails = onNavigateToDetails
            )
            Text("Vista Mediana") // Debug opcional
        }

        WindowSizeClass.Expanded -> {
            HomeExpanded(
                onNavigateToAdd = onNavigateToAdd,
                onNavigateToDetails = onNavigateToDetails
            )
            Text("Vista Expandida") // Debug opcional
        }
    }
}

