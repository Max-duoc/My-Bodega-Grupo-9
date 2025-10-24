package com.example.mybodega_grupo9.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mybodega_grupo9.data.local.ProductoEntity
import com.example.mybodega_grupo9.viewmodel.MovimientoViewModel
import com.example.mybodega_grupo9.viewmodel.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemScreen(
    producto: ProductoEntity,
    vm: ProductoViewModel = viewModel(),
    movimientoVm: MovimientoViewModel = viewModel(),
    onUpdate: () -> Unit
) {
    var nombre by remember { mutableStateOf(producto.nombre) }
    var categoria by remember { mutableStateOf(producto.categoria) }
    var cantidad by remember { mutableStateOf(producto.cantidad.toString()) }
    var descripcion by remember { mutableStateOf(producto.descripcion ?: "") }
    var ubicacion by remember { mutableStateOf(producto.ubicacion ?: "") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Editar Producto") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
            OutlinedTextField(value = categoria, onValueChange = { categoria = it }, label = { Text("Categoría") })
            OutlinedTextField(value = cantidad, onValueChange = { cantidad = it }, label = { Text("Cantidad") })
            OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") })
            OutlinedTextField(value = ubicacion, onValueChange = { ubicacion = it }, label = { Text("Ubicación") })

            Spacer(Modifier.height(20.dp))

            Button(onClick = {
                val updated = producto.copy(
                    nombre = nombre,
                    categoria = categoria,
                    cantidad = cantidad.toIntOrNull() ?: 0,
                    descripcion = descripcion.ifBlank { null },
                    ubicacion = ubicacion.ifBlank { null }
                )
                vm.actualizarProducto(updated)
                movimientoVm.registrarMovimiento("Editar", producto.nombre)

                onUpdate()
            }) {
                Text("Guardar cambios")
            }
        }
    }
}
