package com.example.mybodega_grupo9.ui.screen


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.mybodega_grupo9.data.local.ProductoEntity
import com.example.mybodega_grupo9.model.Producto
import com.example.mybodega_grupo9.viewmodel.MovimientoViewModel
import com.example.mybodega_grupo9.viewmodel.ProductoViewModel
import java.io.File
import java.io.FileOutputStream

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
    var imagenUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val categorias = listOf("Alimentos", "Limpieza", "Herramientas", "Electrónica", "Otros")

    val context = LocalContext.current
    val cameraPermission = Manifest.permission.CAMERA
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }


    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            val uri = saveImageToCache(context, it)
            imagenUri = uri
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imagenUri = uri
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Agregar Producto") }) }) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Previsualización de imagen
            imagenUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                val imageFile = remember {
                    File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
                }
                val imageUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", imageFile)

                val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                    if (success) imagenUri = imageUri
                }

                OutlinedButton(onClick = {
                    if (ContextCompat.checkSelfPermission(context, cameraPermission)
                        == PackageManager.PERMISSION_GRANTED) {
                        cameraLauncher.launch(imageUri)
                    } else {
                        permissionLauncher.launch(cameraPermission)
                    }
                }) {
                    Icon(Icons.Default.Camera, contentDescription = "Cámara")
                    Spacer(Modifier.width(6.dp))
                    Text("Tomar foto")
                }


                OutlinedButton(onClick = { galleryLauncher.launch("image/*") }) {
                    Icon(Icons.Default.Photo, contentDescription = "Galería")
                    Spacer(Modifier.width(6.dp))
                    Text("Elegir imagen")
                }
            }

            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre *") })
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
                    categorias.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                categoria = cat
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(value = cantidad, onValueChange = { cantidad = it }, label = { Text("Cantidad *") })
            OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción (opcional)") })
            OutlinedTextField(value = ubicacion, onValueChange = { ubicacion = it }, label = { Text("Ubicación (opcional)") })

            Spacer(Modifier.height(12.dp))
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
                                ubicacion = ubicacion.ifBlank { null },
                                imagenUri = imagenUri?.toString()
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

fun saveImageToCache(context: Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
    FileOutputStream(file).use { out -> bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out) }
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}


