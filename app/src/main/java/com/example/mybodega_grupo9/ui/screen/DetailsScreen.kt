package com.example.mybodega_grupo9.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mybodega_grupo9.viewmodel.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    productId: Int,
    vm: ProductoViewModel = viewModel()
) {
    val producto = vm.obtenerProductoPorId(productId)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Detalle del Producto") })
        }
    ) { padding ->
        if (producto == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Producto no encontrado", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Nombre: ${producto.nombre}",
                    style = MaterialTheme.typography.titleLarge
                )
                Text("Categoría: ${producto.categoria}")
                Text("Cantidad: ${producto.cantidad}")
                Text("Descripción: ${producto.descripcion}")

                // NUEVO CAMPO — UBICACIÓN
                Text(
                    text = "Ubicación: ${producto.ubicacion ?: "No registrada"}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
