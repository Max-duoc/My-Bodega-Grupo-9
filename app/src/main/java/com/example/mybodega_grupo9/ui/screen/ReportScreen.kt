package com.example.mybodega_grupo9.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mybodega_grupo9.viewmodel.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(navController: NavController, vm: ProductoViewModel = viewModel()) {
    val productos by vm.productos.collectAsState()


    val totalProductos = productos.size
    val totalCantidad = productos.sumOf { it.cantidad }
    val categorias = productos.groupBy { it.categoria }
    val masUsada = categorias.maxByOrNull { it.value.size }?.key ?: "Sin categoría"

    Scaffold(
        topBar = { TopAppBar(title = { Text("Reportes de Inventario") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("📦 Total de productos: $totalProductos")
            Text("📊 Categoría más usada: $masUsada")
            Text("📈 Stock total acumulado: $totalCantidad")
            Spacer(Modifier.height(20.dp))
            Text("Categorías registradas:")
            categorias.forEach { (categoria, lista) ->
                Text("- $categoria (${lista.size})")
            }
        }
    }
}
