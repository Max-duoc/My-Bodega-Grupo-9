package com.example.mybodega_grupo9.ui.screen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.example.mybodega_grupo9.utils.WindowSizeClass
import com.example.mybodega_grupo9.utils.rememberWindowSizeClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAdd: () -> Unit,
    onNavigateToDetails: () -> Unit,
    onNavigateToReport: () -> Unit,
    onNavigateToMovimientos: () -> Unit
) {
    val windowSizeInfo = rememberWindowSizeClass()

    when (windowSizeInfo.widthSizeClass) {
        WindowSizeClass.Compact -> HomeCompact(
            onNavigateToAdd = onNavigateToAdd,
            onNavigateToDetails = onNavigateToDetails,
            onNavigateToReport = onNavigateToReport,
            onNavigateToMovimientos = onNavigateToMovimientos
        )
        WindowSizeClass.Medium -> HomeMedium(
            onNavigateToAdd = onNavigateToAdd,
            onNavigateToDetails = onNavigateToDetails,
            onNavigateToReport = onNavigateToReport,
            onNavigateToMovimientos = onNavigateToMovimientos
        )
        WindowSizeClass.Expanded -> HomeExpanded(
            onNavigateToAdd = onNavigateToAdd,
            onNavigateToDetails = onNavigateToDetails,
            onNavigateToReport = onNavigateToReport,
            onNavigateToMovimientos = onNavigateToMovimientos
        )
    }
}

