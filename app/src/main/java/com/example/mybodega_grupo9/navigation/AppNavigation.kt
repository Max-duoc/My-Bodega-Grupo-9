package com.example.mybodega_grupo9.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mybodega_grupo9.ui.Screen.RegistroScreen
import com.example.mybodega_grupo9.ui.Screen.ResumenScreen
import com.example.mybodega_grupo9.viewmodel.UsuarioViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Aquí creamos el ViewModel una sola vez para compartirlo [cite: 337]
    val usuarioViewModel: UsuarioViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "registro"
    ) {
        composable(route = "registro") {
            RegistroScreen(navController, usuarioViewModel)
        }
        composable(route = "resumen") {
            ResumenScreen(usuarioViewModel)
        }
    }
}