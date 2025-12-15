package com.example.mybodega_grupo9.ui.screen

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.mybodega_grupo9.data.local.ProductoEntity
import com.example.mybodega_grupo9.viewmodel.ProductoViewModel
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import com.example.mybodega_grupo9.viewmodel.SyncState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    navController: NavController,
    vm: ProductoViewModel = viewModel()
) {
    val productos by vm.productos.collectAsState()
    val message by vm.message.collectAsState()
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val syncState by vm.syncState.collectAsState()

    // 🔥 NUEVO: Estados para productos locales y en proceso de subida
    val productosLocales by vm.productosLocales.collectAsState()
    val uploadingProducts by vm.uploadingProducts.collectAsState()

    // 🔥 Verificar productos locales al cargar la pantalla
    LaunchedEffect(productos) {
        vm.checkLocalProducts()
    }

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            vm.clearMessage()
        }
    }

    val filtered = productos.filter {
        it.nombre.contains(searchQuery, ignoreCase = true) ||
                it.categoria.contains(searchQuery, ignoreCase = true)
    }

    val totalProductos = productos.size
    val totalStock = productos.sumOf { it.cantidad }
    val categorias = productos.groupBy { it.categoria }.size
    val productosLocalesCount = productosLocales.count { it.value }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Mi Inventario",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                        AnimatedVisibility(
                            visible = syncState !is SyncState.Idle,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            when (val state = syncState) {
                                is SyncState.Syncing -> {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(12.dp),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            strokeWidth = 1.5.dp
                                        )
                                        Text(
                                            "Sincronizando...",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                                alpha = 0.7f
                                            )
                                        )
                                    }
                                }

                                is SyncState.Success -> {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp),
                                            tint = Color(0xFF10B981)
                                        )
                                        Text(
                                            "✓ ${state.uploaded + state.downloaded + state.updated} cambios",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                                alpha = 0.7f
                                            )
                                        )
                                    }
                                }

                                is SyncState.Error -> {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Error,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp),
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                        Text(
                                            state.message,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }

                                else -> {}
                            }
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { vm.syncProductos() },
                        enabled = syncState !is SyncState.Syncing
                    ) {
                        when (syncState) {
                            is SyncState.Syncing -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    strokeWidth = 2.dp
                                )
                            }

                            else -> {
                                Icon(
                                    Icons.Default.Sync,
                                    contentDescription = "Sincronizar todo",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
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
        ) {
            // Dashboard de estadísticas
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = Icons.Default.Inventory,
                    label = "Productos",
                    value = totalProductos.toString(),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Default.ShoppingCart,
                    label = "Stock",
                    value = totalStock.toString(),
                    color = Color(0xFF10B981),
                    modifier = Modifier.weight(1f)
                )
                // 🔥 NUEVO: Mostrar productos locales
                if (productosLocalesCount > 0) {
                    StatCard(
                        icon = Icons.Default.CloudUpload,
                        label = "Sin Subir",
                        value = productosLocalesCount.toString(),
                        color = Color(0xFFF59E0B),
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    StatCard(
                        icon = Icons.Default.Category,
                        label = "Categorías",
                        value = categorias.toString(),
                        color = Color(0xFF8B5CF6),
                        modifier = Modifier.weight(1f)
                    )
                }
            }


            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (filtered.isEmpty()) {
                EmptyState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtered, key = { it.id }) { producto ->
                        ProductCard(
                            producto = producto,
                            isLocal = productosLocales[producto.id] == true,
                            isUploading = uploadingProducts.contains(producto.id),
                            onEdit = { navController.navigate("edit/${producto.id}") },
                            onDelete = { vm.eliminarProducto(producto) },
                            onConsumir = { vm.consumirProducto(producto) },
                            onReabastecer = { vm.reabastecerProducto(producto) },
                            onUpload = { vm.uploadProductToServer(producto) }
                        )
                    }
                }
            }
        }
    }
}


// ============================================
// RESTO DE COMPONENTES (sin cambios)
// ============================================

@Composable
fun StatsRow(
    totalProductos: Int,
    totalStock: Int,
    categorias: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            icon = Icons.Default.Inventory,
            label = "Productos",
            value = totalProductos.toString(),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = Icons.Default.ShoppingCart,
            label = "Stock Total",
            value = totalStock.toString(),
            color = Color(0xFF10B981),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = Icons.Default.Category,
            label = "Categorías",
            value = categorias.toString(),
            color = Color(0xFF8B5CF6),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(90.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Buscar por nombre o categoría...") },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        ),
        singleLine = true
    )
}

@Composable
fun ProductCard(
    producto: ProductoEntity,
    isLocal: Boolean,
    isUploading: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onConsumir: () -> Unit,
    onReabastecer: () -> Unit,
    onUpload: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = producto.nombre,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        // 🔥 NUEVO: Badge de "Sin subir"
                        if (isLocal) {
                            Surface(
                                color = Color(0xFFF59E0B),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "OFFLINE",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))

                    CategoriaBadge(categoria = producto.categoria)
                }

                    StockIndicator(cantidad = producto.cantidad)
                }

                if (!producto.ubicacion.isNullOrBlank()) {
                    Spacer(Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.Place,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = producto.ubicacion,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                producto.imagenUri?.let { uri ->
                    Spacer(Modifier.height(12.dp))
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Imagen de ${producto.nombre}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                if (producto.cantidad <= 2 && producto.cantidad > 0) {
                    Spacer(Modifier.height(12.dp))
                    LowStockAlert()
                }

                Spacer(Modifier.height(16.dp))

                // 🔥 NUEVO: Botón de subir al servidor (solo para productos locales)
                if (isLocal) {
                    Button(
                        onClick = onUpload,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF10B981)
                        ),
                        enabled = !isUploading
                    ) {
                        if (isUploading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Subiendo...")
                        } else {
                            Icon(
                                Icons.Default.CloudUpload,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Subir al Servidor")
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = onConsumir,
                                enabled = producto.cantidad > 0,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (producto.cantidad > 0)
                                            MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                                        else
                                            Color.Transparent
                                    )
                            ) {
                                Icon(
                                    Icons.Default.Remove,
                                    contentDescription = "Consumir",
                                    tint = if (producto.cantidad > 0)
                                        MaterialTheme.colorScheme.error
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                )
                            }

                            IconButton(
                                onClick = onReabastecer,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF10B981).copy(alpha = 0.1f))
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Reabastecer",
                                    tint = Color(0xFF10B981)
                                )
                            }
                        }
                    }

                    FilledTonalButton(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Editar")
                    }

                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Eliminar")
                    }
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Confirmar eliminación") },
                text = { Text("¿Estás seguro de que deseas eliminar '${producto.nombre}'?") },
                confirmButton = {
                    Button(
                        onClick = {
                            onDelete()
                            showDeleteDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }


    @Composable
    fun CategoriaBadge(categoria: String) {
        val (color, icon) = when (categoria.lowercase()) {
            "alimentos", "comida" -> Pair(Color(0xFF10B981), Icons.Default.Restaurant)
            "limpieza" -> Pair(Color(0xFF3B82F6), Icons.Default.CleaningServices)
            "cuidado personal" -> Pair(Color(0xFF8B5CF6), Icons.Default.Face)
            "herramientas" -> Pair(Color(0xFFF59E0B), Icons.Default.Build)
            else -> Pair(Color(0xFF6B7280), Icons.Default.Category)
        }

        Surface(
            color = color.copy(alpha = 0.15f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = categoria,
                    style = MaterialTheme.typography.labelSmall,
                    color = color,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }

    @Composable
    fun StockIndicator(cantidad: Int) {
        val (backgroundColor, textColor, label) = when {
            cantidad == 0 -> Triple(
                Color(0xFFEF4444).copy(alpha = 0.1f),
                Color(0xFFEF4444),
                "Agotado"
            )

            cantidad <= 2 -> Triple(
                Color(0xFFF59E0B).copy(alpha = 0.1f),
                Color(0xFFF59E0B),
                "Stock bajo"
            )

            else -> Triple(
                Color(0xFF10B981).copy(alpha = 0.1f),
                Color(0xFF10B981),
                "En stock"
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(backgroundColor, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = cantidad.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = textColor
            )
        }
    }

    @Composable
    fun LowStockAlert() {
        Surface(
            color = Color(0xFFFEF3C7),
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
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Stock bajo - Considera reabastecer pronto",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF92400E),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    @Composable
    fun EmptyState(modifier: Modifier = Modifier) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Inventory,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "No se encontraron productos",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Intenta con otra búsqueda",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
