package com.example.mybodega_grupo9.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.mybodega_grupo9.data.local.ProductoEntity
import com.example.mybodega_grupo9.viewmodel.ProductoViewModel
import com.example.mybodega_grupo9.viewmodel.MovimientoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    navController: NavController,
    vm: ProductoViewModel,
    movimientoVm: MovimientoViewModel,
    onEdit: (Int) -> Unit
) {
    val productos by vm.productos.collectAsState()

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("Todas") }
    var expanded by remember { mutableStateOf(false) }

    val categorias = listOf("Todas") + productos.map { it.categoria }.distinct()

    val filtered = productos.filter {
        (selectedCategory == "Todas" || it.categoria == selectedCategory) &&
                (it.nombre.contains(searchQuery, ignoreCase = true) ||
                        it.descripcion?.contains(searchQuery, true) == true)
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var productoAEliminar by remember { mutableStateOf<ProductoEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventario") },
                actions = {
                    IconButton(onClick = { navController.navigate("movimientos") }) {
                        Icon(Icons.Default.History, contentDescription = "Ver movimientos")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // 🔍 Buscador
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar producto") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // 🧩 Filtro
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    readOnly = true,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    categorias.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                selectedCategory = cat
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay productos que coincidan.")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(filtered) { producto ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        producto.nombre,
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text("x${producto.cantidad}", fontWeight = FontWeight.Bold)
                                }

                                Text("Categoría: ${producto.categoria}")
                                producto.ubicacion?.let { Text("Ubicación: $it") }
                                producto.descripcion?.let { Text("Descripción: $it") }

                                Spacer(Modifier.height(8.dp))

                                producto.imagenUri?.let { uri ->
                                    Image(
                                        painter = rememberAsyncImagePainter(uri),
                                        contentDescription = "Imagen de ${producto.nombre}",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(160.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                Spacer(Modifier.height(8.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Button(
                                        onClick = {
                                            vm.consumirProducto(producto)
                                            movimientoVm.registrarMovimiento("Consumo", producto.nombre)
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.errorContainer
                                        )
                                    ) { Text("Consumir") }

                                    Button(
                                        onClick = {
                                            vm.reabastecerProducto(producto)
                                            movimientoVm.registrarMovimiento("Reabastecimiento", producto.nombre)
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                                        )
                                    ) { Text("Reabastecer") }

                                    IconButton(onClick = { onEdit(producto.id) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                                    }

                                    IconButton(onClick = {
                                        productoAEliminar = producto
                                        showDeleteDialog = true
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 🔴 Diálogo de confirmación de eliminación
            if (showDeleteDialog && productoAEliminar != null) {
                AlertDialog(
                    onDismissRequest = {
                        showDeleteDialog = false
                        productoAEliminar = null
                    },
                    title = { Text("Eliminar producto") },
                    text = { Text("¿Seguro que deseas eliminar \"${productoAEliminar!!.nombre}\"?") },
                    confirmButton = {
                        TextButton(onClick = {
                            vm.eliminarProducto(productoAEliminar!!)
                            movimientoVm.registrarMovimiento("Eliminar", productoAEliminar!!.nombre)
                            showDeleteDialog = false
                            productoAEliminar = null
                        }) {
                            Text("Eliminar", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showDeleteDialog = false
                            productoAEliminar = null
                        }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}

