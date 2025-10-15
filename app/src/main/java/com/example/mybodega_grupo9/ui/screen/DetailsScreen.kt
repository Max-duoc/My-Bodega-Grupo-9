package com.example.mybodega_grupo9.ui.screen


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.mybodega_grupo9.viewmodel.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    productId: Int = 0,
    vm: ProductoViewModel = viewModel()
) {
    val productos by vm.productos.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Productos almacenados") }) }
    ) { padding ->
        if (productos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay productos registrados.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(productos) { producto ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            // ✅ Mostrar imagen si existe
                            producto.imagenUri?.let { uri ->
                                Image(
                                    painter = rememberAsyncImagePainter(uri),
                                    contentDescription = "Imagen de ${producto.nombre}",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .padding(bottom = 12.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Text("Nombre: ${producto.nombre}", style = MaterialTheme.typography.titleMedium)
                            Text("Categoría: ${producto.categoria}")
                            Text("Cantidad: ${producto.cantidad}")
                            Text("Descripción: ${producto.descripcion}")
                            Text("Ubicación: ${producto.ubicacion ?: "No registrada"}")
                        }
                    }
                }
            }
        }
    }
}