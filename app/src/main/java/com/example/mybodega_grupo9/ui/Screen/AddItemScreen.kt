package com.example.mybodega_grupo9.ui.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mybodega_grupo9.model.Producto
import com.example.mybodega_grupo9.viewmodel.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    vm: ProductoViewModel = viewModel(),
    onSave: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") } // NUEVO CAMPO

    Scaffold(
        topBar = { TopAppBar(title = { Text("Agregar Producto") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre *") },
                singleLine = true
            )

            OutlinedTextField(
                value = categoria,
                onValueChange = { categoria = it },
                label = { Text("Categoría *") },
                singleLine = true
            )

            OutlinedTextField(
                value = cantidad,
                onValueChange = { cantidad = it },
                label = { Text("Cantidad *") },
                singleLine = true
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                singleLine = false
            )

            // NUEVO CAMPO OPCIONAL
            OutlinedTextField(
                value = ubicacion,
                onValueChange = { ubicacion = it },
                label = { Text("Ubicación (opcional)") },
                singleLine = true
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    if (nombre.isNotBlank() && cantidad.isNotBlank()) {
                        vm.agregarProducto(
                            Producto(
                                id = (0..100000).random(),
                                nombre = nombre,
                                categoria = categoria,
                                cantidad = cantidad.toInt(),
                                descripcion = descripcion,
                                ubicacion = ubicacion.ifBlank { null } // ← si está vacío, guarda null
                            )
                        )
                        onSave()
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Guardar")
            }
        }
    }
}

