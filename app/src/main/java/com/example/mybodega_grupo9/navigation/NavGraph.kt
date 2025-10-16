package com.example.mybodega_grupo9.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mybodega_grupo9.ui.screen.AddItemScreen
import com.example.mybodega_grupo9.ui.screen.DetailsScreen
import com.example.mybodega_grupo9.ui.screen.EditItemScreen
import com.example.mybodega_grupo9.ui.screen.HomeScreen
import com.example.mybodega_grupo9.ui.screen.ReportScreen
//import com.example.mybodega_grupo9.ui.screen.ReportScreen
import com.example.mybodega_grupo9.viewmodel.ProductoViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AppNavGraph(navController: NavHostController) {

    // ViewModel disponible en todas las rutas
    val vm: ProductoViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        // Pantalla principal
        composable("home") {
            HomeScreen(
                onNavigateToAdd = { navController.navigate("add") },
                onNavigateToDetails = { navController.navigate("details") } ,
                onNavigateToReport = { navController.navigate("report") }
            )
        }

        // Agregar producto
        composable("add") {
            AddItemScreen(
                vm = vm,
                onSave = { navController.navigate("details") }
            )
        }

        // Ver inventario
        composable("details") {
            DetailsScreen(
                navController = navController,
                vm = vm
            )
        }

        // Editar producto (se pasa ID como argumento)
        composable("edit/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
            val productos = vm.productos.value
            val productoSeleccionado = productos.find { it.id == id }

            if (productoSeleccionado != null) {
                EditItemScreen(
                    producto = productoSeleccionado,
                    vm = vm,
                    onUpdate = { navController.popBackStack() }
                )
            } else {
                // Si no se encuentra, volver atrás
                navController.popBackStack()
            }
        }

        // 📊 Reportes de inventario
        composable("report") {
            ReportScreen(
                navController = navController,
                vm = vm
            )
        }
    }
}

