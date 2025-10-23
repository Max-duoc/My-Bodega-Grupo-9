package com.example.mybodega_grupo9.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.mybodega_grupo9.data.local.ProductoEntity
import com.example.mybodega_grupo9.viewmodel.MovimientoViewModel
import com.example.mybodega_grupo9.viewmodel.ProductoViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    vm: ProductoViewModel = viewModel(),
    movimientoVm: MovimientoViewModel = viewModel(),
    onSave: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    // ✅ Launcher para abrir la cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // Guardamos la imagen en memoria temporal
        }
    }

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
            // --- CAMPOS DE TEXTO ---
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
            OutlinedTextField(
                value = ubicacion,
                onValueChange = { ubicacion = it },
                label = { Text("Ubicación (opcional)") },
                singleLine = true
            )

            // --- BOTÓN CÁMARA ---
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = {
                val photoFile = File(context.cacheDir, "temp_photo.jpg")
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    photoFile
                )
                imageUri = uri
                cameraLauncher.launch(uri)
            }) {
                Text("Tomar foto del producto")
            }

            // --- VISTA PREVIA DE IMAGEN ---
            imageUri?.let { uri ->
                Spacer(modifier = Modifier.height(10.dp))
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Imagen del producto",
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                )
            }

            // --- BOTÓN GUARDAR ---
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    if (nombre.isNotBlank() && cantidad.isNotBlank()) {
                        // ✅ Guardar en base de datos Room
                        val producto = ProductoEntity(
                            nombre = nombre,
                            categoria = categoria,
                            cantidad = cantidad.toInt(),
                            descripcion = descripcion.ifBlank { null },
                            ubicacion = ubicacion.ifBlank { null },
                            imagenUri = imageUri?.toString()
                        )
                        movimientoVm.registrarMovimiento("Agregar", producto.nombre)
                        vm.agregarProducto(producto) // importa correctamente


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
