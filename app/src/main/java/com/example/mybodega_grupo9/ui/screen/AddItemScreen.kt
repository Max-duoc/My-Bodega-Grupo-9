package com.example.mybodega_grupo9.ui.screen


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.mybodega_grupo9.data.local.ProductoEntity
import com.example.mybodega_grupo9.model.Producto
import com.example.mybodega_grupo9.viewmodel.MovimientoViewModel
import com.example.mybodega_grupo9.viewmodel.ProductoViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    vm: ProductoViewModel = viewModel(),
    onSave: () -> Unit
) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var categoria by rememberSaveable { mutableStateOf("") }
    var cantidad by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var ubicacion by rememberSaveable { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val categorias = listOf("Alimentos", "Limpieza", "Herramientas", "Electrónica", "Otros")

    Scaffold(topBar = { TopAppBar(title = { Text("Agregar Producto") }) }) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre *") },
                singleLine = true
            )

            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = categoria,
                    onValueChange = {},
                    label = { Text("Categoría *") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    categorias.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                categoria = opcion
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = cantidad,
                onValueChange = { cantidad = it },
                label = { Text("Cantidad *") },
                singleLine = true
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción (opcional)") }
            )

            OutlinedTextField(
                value = ubicacion,
                onValueChange = { ubicacion = it },
                label = { Text("Ubicación (opcional)") }
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    if (nombre.isNotBlank() && categoria.isNotBlank() && cantidad.isNotBlank()) {
                        vm.agregarProducto(
                            ProductoEntity(
                                id = (0..100000).random(),
                                nombre = nombre,
                                categoria = categoria,
                                cantidad = cantidad.toInt(),
                                descripcion = descripcion.ifBlank { null },
                                ubicacion = ubicacion.ifBlank { null }
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
