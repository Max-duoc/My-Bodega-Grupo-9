package com.example.mybodega_grupo9.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mybodega_grupo9.ui.screen.AddItemScreen
import com.example.mybodega_grupo9.ui.screen.DetailsScreen
import com.example.mybodega_grupo9.ui.screen.HomeScreen
import com.example.mybodega_grupo9.viewmodel.ProductoViewModel

@Composable
fun MyBodegaNavHost(navController: NavHostController) {
    // ✅ Crear el ViewModel una sola vez para compartirlo entre todas las pantallas
    val productoViewModel: ProductoViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToAdd = { navController.navigate(Screen.AddItem.route) },
                onNavigateToDetails = { navController.navigate(Screen.Details.route) }
            )
        }

        composable(Screen.AddItem.route) {
            // ✅ Pasar el ViewModel compartido
            AddItemScreen(
                vm = productoViewModel,
                onSave = {
                    navController.popBackStack() // Volver a Home
                }
            )
        }

        composable(Screen.Details.route) {
            // ✅ Pasar el ViewModel compartido
            DetailsScreen(
                productId = 0,
                vm = productoViewModel
            )
        }

        // Si quieres mantener la ruta con ID (opcional)
        composable("${Screen.Details.route}/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
            DetailsScreen(
                productId = id,
                vm = productoViewModel
            )
        }
    }
}