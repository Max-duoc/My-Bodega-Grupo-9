package com.example.mybodega_grupo9.ui.screen

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var showCategoryMenu by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }

    // Validaciones
    var nombreError by remember { mutableStateOf(false) }
    var categoriaError by remember { mutableStateOf(false) }
    var cantidadError by remember { mutableStateOf(false) }

    // Detección de cambios
    val hasChanges = nombre != producto.nombre ||
            categoria != producto.categoria ||
            cantidad != producto.cantidad.toString() ||
            descripcion != (producto.descripcion ?: "") ||
            ubicacion != (producto.ubicacion ?: "")

    val categoriasPredefinidas = listOf(
        "Alimentos" to Icons.Default.Restaurant,
        "Limpieza" to Icons.Default.CleaningServices,
        "Cuidado Personal" to Icons.Default.Face,
        "Herramientas" to Icons.Default.Build,
        "Otros" to Icons.Default.Category
    )

    fun validarYGuardar() {
        nombreError = nombre.isBlank()
        categoriaError = categoria.isBlank()
        cantidadError = cantidad.isBlank() || cantidad.toIntOrNull() == null

        if (!nombreError && !categoriaError && !cantidadError) {
            val updated = producto.copy(
                nombre = nombre.trim(),
                categoria = categoria,
                cantidad = cantidad.toInt(),
                descripcion = descripcion.trim().ifBlank { null },
                ubicacion = ubicacion.trim().ifBlank { null }
            )
            vm.actualizarProducto(updated)
            movimientoVm.registrarMovimiento("Editar", producto.nombre)
            onUpdate()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Editar Producto",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                        Text(
                            producto.nombre,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onUpdate) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Indicador de cambios
            AnimatedVisibility(
                visible = hasChanges,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Tienes cambios sin guardar",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Sección: Información básica
            SectionHeader(
                title = "Información Básica",
                icon = Icons.Default.Info
            )

            // Campo: Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    nombreError = false
                },
                label = { Text("Nombre del producto *") },
                leadingIcon = {
                    Icon(Icons.Default.Label, contentDescription = null)
                },
                isError = nombreError,
                supportingText = {
                    if (nombreError) Text("El nombre es obligatorio")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Campo: Categoría
            ExposedDropdownMenuBox(
                expanded = showCategoryMenu,
                onExpandedChange = { showCategoryMenu = it }
            ) {
                OutlinedTextField(
                    value = categoria,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría *") },
                    leadingIcon = {
                        Icon(Icons.Default.Category, contentDescription = null)
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryMenu)
                    },
                    isError = categoriaError,
                    supportingText = {
                        if (categoriaError) Text("Selecciona una categoría")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = showCategoryMenu,
                    onDismissRequest = { showCategoryMenu = false }
                ) {
                    categoriasPredefinidas.forEach { (cat, icon) ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
                                    Text(cat)
                                }
                            },
                            onClick = {
                                categoria = cat
                                categoriaError = false
                                showCategoryMenu = false
                            }
                        )
                    }
                }
            }

            // Campo: Cantidad con controles
            Column {
                Text(
                    "Cantidad *",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón decrementar
                    FilledIconButton(
                        onClick = {
                            val current = cantidad.toIntOrNull() ?: 0
                            if (current > 0) cantidad = (current - 1).toString()
                        },
                        modifier = Modifier.size(56.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            contentDescription = "Disminuir",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }

                    // Campo de cantidad
                    OutlinedTextField(
                        value = cantidad,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() }) {
                                cantidad = it
                                cantidadError = false
                            }
                        },
                        modifier = Modifier.weight(1f),
                        textStyle = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        ),
                        isError = cantidadError,
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    // Botón incrementar
                    FilledIconButton(
                        onClick = {
                            val current = cantidad.toIntOrNull() ?: 0
                            cantidad = (current + 1).toString()
                        },
                        modifier = Modifier.size(56.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Aumentar",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                if (cantidadError) {
                    Text(
                        "Ingresa una cantidad válida",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }

            // Sección: Detalles adicionales
            Spacer(Modifier.height(8.dp))
            SectionHeader(
                title = "Detalles Adicionales",
                icon = Icons.Default.Description
            )

            // Campo: Descripción
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción (opcional)") },
                leadingIcon = {
                    Icon(Icons.Default.Notes, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 4
            )

            // Campo: Ubicación
            OutlinedTextField(
                value = ubicacion,
                onValueChange = { ubicacion = it },
                label = { Text("Ubicación (opcional)") },
                leadingIcon = {
                    Icon(Icons.Default.Place, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Botones de acción
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { showSaveDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                enabled = hasChanges
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Guardar Cambios", fontSize = 16.sp)
            }

            OutlinedButton(
                onClick = onUpdate,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.Cancel,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Cancelar")
            }

            Spacer(Modifier.height(32.dp))
        }
    }

    // Diálogo de confirmación
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Text("Confirmar cambios")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("¿Deseas guardar los cambios realizados en este producto?")

                    if (nombre != producto.nombre) {
                        Text(
                            "• Nombre: ${producto.nombre} → $nombre",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (cantidad != producto.cantidad.toString()) {
                        Text(
                            "• Cantidad: ${producto.cantidad} → $cantidad",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        validarYGuardar()
                        showSaveDialog = false
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancelar")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}