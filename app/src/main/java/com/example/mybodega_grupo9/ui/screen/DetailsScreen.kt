package com.example.mybodega_grupo9.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.mybodega_grupo9.viewmodel.MovimientoViewModel
import com.example.mybodega_grupo9.viewmodel.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    navController: NavController,
    vm: ProductoViewModel = viewModel() ,
    movimientoVm: MovimientoViewModel = viewModel()

) {
    val productos by vm.productos.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    // Filtrado en tiempo real
    val filtered = productos.filter {
        it.nombre.contains(searchQuery, ignoreCase = true) ||
                it.categoria.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Inventario") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Campo de búsqueda SIEMPRE visible
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar producto...") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            if (filtered.isEmpty()) {
                Text("No hay productos que coincidan con la búsqueda.")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f) // ← evita que tape el filtro
                ) {
                    items(filtered) { producto ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(producto.nombre, style = MaterialTheme.typography.titleMedium)
                                Text("Categoría: ${producto.categoria}")
                                Text("Cantidad: ${producto.cantidad}")
                                Text("Ubicación: ${producto.ubicacion ?: "No registrada"}")

                                producto.imagenUri?.let {
                                    Image(
                                        painter = rememberAsyncImagePainter(it),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .height(150.dp)
                                            .fillMaxWidth()
                                    )
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            vm.consumirProducto(producto)
                                        },
                                        enabled = producto.cantidad > 0
                                    ) {
                                        Text("Consumir")
                                    }

                                    Button(
                                        onClick = {
                                            vm.reabastecerProducto(producto)
                                        }
                                    ) {
                                        Text("Reabastecer")
                                    }
                                }

                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(onClick = {
                                        navController.navigate("edit/${producto.id}")
                                    }) {
                                        Text("Editar")
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Button(onClick = {
                                        vm.eliminarProducto(producto)
                                        movimientoVm.registrarMovimiento("Eliminar", producto.nombre)
                                    }) {
                                        Text("Eliminar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
