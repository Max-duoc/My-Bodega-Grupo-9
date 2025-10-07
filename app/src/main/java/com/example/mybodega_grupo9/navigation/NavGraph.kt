package com.example.mybodega_grupo9.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mybodega_grupo9.ui.Screen.AdaptiveHome

@Composable
fun MyBodegaNavHost(startDestination: String = Screen.Home.route) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Home.route) {
            AdaptiveHome(onAdd = { navController.navigate(Screen.AddItem.route) })
        }
        composable(Screen.AddItem.route) {
            AddItemScreen(onSaved = { navController.popBackStack() })
        }
        composable("details/{itemId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("itemId")?.toIntOrNull() ?: 0
            DetailsScreen(itemId = id)
        }
    }
}

@Composable
fun DetailsScreen(itemId: Int) {
    TODO("Not yet implemented")
}

@Composable
fun AddItemScreen(onSaved: () -> Boolean) {
    TODO("Not yet implemented")
}
