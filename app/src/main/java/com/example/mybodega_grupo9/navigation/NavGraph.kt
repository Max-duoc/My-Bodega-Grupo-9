package com.example.mybodega_grupo9.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mybodega_grupo9.ui.screen.AddItemScreen
import com.example.mybodega_grupo9.ui.screen.DetailsScreen
import com.example.mybodega_grupo9.ui.screen.HomeScreen

@Composable
fun MyBodegaNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToAdd = { navController.navigate(Screen.AddItem.route) },
                onNavigateToDetails = { navController.navigate(Screen.Details.route) } // ✅ nueva ruta
            )
        }
        composable(Screen.AddItem.route) {
            AddItemScreen(onSave = { navController.navigate(Screen.Home.route) })
        }
        composable("${Screen.Details.route}/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
            DetailsScreen(productId = id)
        }
        composable(Screen.Details.route) {
            DetailsScreen(productId = 0) // verás la lista completa (actualizaremos abajo)
        }
    }
}

