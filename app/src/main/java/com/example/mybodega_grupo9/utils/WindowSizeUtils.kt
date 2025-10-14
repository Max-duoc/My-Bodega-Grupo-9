package com.example.mybodega_grupo9.utils


import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration

/**
 * Representa los tamaños de ventana para adaptar el diseño según el ancho disponible.
 */
enum class WindowSizeClass {
    Compact,
    Medium,
    Expanded
}

/**
 * Contiene la clase de ancho de la ventana actual.
 */
data class WindowSizeInfo(
    val widthSizeClass: WindowSizeClass
)

/**
 * Calcula el tamaño actual de la ventana (Compact, Medium o Expanded)
 * basándose en el ancho de pantalla en dp.
 */
@Composable
fun rememberWindowSizeClass(): WindowSizeInfo {
    val configuration = LocalConfiguration.current

    // recordamos el ancho de pantalla actual
    val screenWidthDp = configuration.screenWidthDp

    // Clasificamos según el tamaño
    val widthClass = remember(screenWidthDp) {
        when {
            screenWidthDp < 600 -> WindowSizeClass.Compact
            screenWidthDp < 840 -> WindowSizeClass.Medium
            else -> WindowSizeClass.Expanded
        }
    }

    return WindowSizeInfo(widthSizeClass = widthClass)
}

