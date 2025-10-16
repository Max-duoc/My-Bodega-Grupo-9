package com.example.mybodega_grupo9.ui.screen


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.mybodega_grupo9.viewmodel.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    navController: NavController,
    productId: Int = 0,
    vm: ProductoViewModel = viewModel()
) {
    val productos by vm.productos.collectAsState()

    LazyColumn {

        items(productos) { producto ->
            // Dentro de tu LazyColumn (DetailsScreen)
            Card(
                onClick = { navController.navigate("edit/${producto.id}") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ){
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
                                .height(160.dp)
                                .fillMaxWidth()
                        )
                    }
                    Button(
                        onClick = { vm.eliminarProducto(producto) },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Eliminar")
                    }
                }
            }
        }
    }
}