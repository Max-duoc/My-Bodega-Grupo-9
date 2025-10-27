// ============================================
// ReportScreen.kt - VERSIÓN MEJORADA
// ============================================
package com.example.mybodega_grupo9.ui.screen

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mybodega_grupo9.viewmodel.ProductoViewModel
import com.example.mybodega_grupo9.viewmodel.MovimientoViewModel
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    navController: NavController,
    vm: ProductoViewModel = viewModel()
) {
    val productos by vm.productos.collectAsState()
    val movimientos by movimientoVm.movimientos.collectAsState()

    // Cálculos de estadísticas
    val totalProductos = productos.size
    val totalStock = productos.sumOf { it.cantidad }
    val categorias = productos.groupBy { it.categoria }
    val categoriaMasUsada = categorias.maxByOrNull { it.value.size }?.key ?: "Sin categoría"
    val productosMayorStock = productos.sortedByDescending { it.cantidad }.take(5)
    val productosStockBajo = productos.filter { it.cantidad <= 2 && it.cantidad > 0 }
    val productosAgotados = productos.filter { it.cantidad == 0 }
    val promedioStock = if (totalProductos > 0) totalStock.toFloat() / totalProductos else 0f

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Reportes e Insights",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
        LazyColumn(
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
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Resumen general
            item {
                Text(
                    "Resumen General",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ReportMetricCard(
                        title = "Total",
                        value = totalProductos.toString(),
                        subtitle = "productos",
                        icon = Icons.Default.Inventory,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    ReportMetricCard(
                        title = "Stock",
                        value = totalStock.toString(),
                        subtitle = "unidades",
                        icon = Icons.Default.ShoppingCart,
                        color = Color(0xFF10B981),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ReportMetricCard(
                        title = "Promedio",
                        value = String.format("%.1f", promedioStock),
                        subtitle = "por producto",
                        icon = Icons.Default.TrendingUp,
                        color = Color(0xFF3B82F6),
                        modifier = Modifier.weight(1f)
                    )
                    ReportMetricCard(
                        title = "Categorías",
                        value = categorias.size.toString(),
                        subtitle = "diferentes",
                        icon = Icons.Default.Category,
                        color = Color(0xFF8B5CF6),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Alertas
            if (productosAgotados.isNotEmpty() || productosStockBajo.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Alertas de Inventario",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (productosAgotados.isNotEmpty()) {
                    item {
                        AlertCard(
                            title = "Productos Agotados",
                            count = productosAgotados.size,
                            icon = Icons.Default.ErrorOutline,
                            color = MaterialTheme.colorScheme.error,
                            items = productosAgotados.map { it.nombre }
                        )
                    }
                }

                if (productosStockBajo.isNotEmpty()) {
                    item {
                        AlertCard(
                            title = "Stock Bajo (≤ 2 unidades)",
                            count = productosStockBajo.size,
                            icon = Icons.Default.Warning,
                            color = Color(0xFFF59E0B),
                            items = productosStockBajo.map { "${it.nombre} (${it.cantidad})" }
                        )
                    }
                }
            }

            // Distribución por categoría
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Distribución por Categorías",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(categorias.entries.sortedByDescending { it.value.size }) { (categoria, productos) ->
                CategoryCard(
                    categoria = categoria,
                    cantidad = productos.size,
                    stockTotal = productos.sumOf { it.cantidad },
                    porcentaje = (productos.size.toFloat() / totalProductos * 100).toInt(),
                    esMasUsada = categoria == categoriaMasUsada
                )
            }

            // Top 5 productos con mayor stock
            if (productosMayorStock.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Top 5 Mayor Stock",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(productosMayorStock) { producto ->
                    TopProductCard(
                        nombre = producto.nombre,
                        categoria = producto.categoria,
                        cantidad = producto.cantidad,
                        ranking = productosMayorStock.indexOf(producto) + 1
                    )
                }
            }

            // Espaciador final
            item {
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

// ============================================
// COMPONENTE: Card de métrica principal (ReportScreen)
// ============================================
@Composable
fun ReportMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(140.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ============================================
// COMPONENTE: Card de alerta
// ============================================
@Composable
fun AlertCard(
    title: String,
    count: Int,
    icon: ImageVector,
    color: Color,
    items: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = color
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(48.dp)
                                .padding(12.dp)
                        )
                    }

                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = color
                        )
                        Text(
                            text = "$count ${if (count == 1) "producto" else "productos"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Contraer" else "Expandir",
                        tint = color
                    )
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(color = color.copy(alpha = 0.3f))

                    items.forEach { item ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Circle,
                                contentDescription = null,
                                modifier = Modifier.size(8.dp),
                                tint = color
                            )
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============================================
// COMPONENTE: Card de categoría
// ============================================
@Composable
fun CategoryCard(
    categoria: String,
    cantidad: Int,
    stockTotal: Int,
    porcentaje: Int,
    esMasUsada: Boolean
) {
    val (color, icon) = when (categoria.lowercase()) {
        "alimentos", "comida" -> Pair(Color(0xFF10B981), Icons.Default.Restaurant)
        "limpieza" -> Pair(Color(0xFF3B82F6), Icons.Default.CleaningServices)
        "cuidado personal" -> Pair(Color(0xFF8B5CF6), Icons.Default.Face)
        "herramientas" -> Pair(Color(0xFFF59E0B), Icons.Default.Build)
        else -> Pair(Color(0xFF6B7280), Icons.Default.Category)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (esMasUsada) 6.dp else 2.dp
        ),
        shape = RoundedCornerShape(16.dp),
        border = if (esMasUsada) CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(color, color.copy(alpha = 0.5f))),
            width = 2.dp
        ) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = color.copy(alpha = 0.15f)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier
                            .size(56.dp)
                            .padding(14.dp)
                    )
                }

                Column {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = categoria,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (esMasUsada) {
                            Surface(
                                color = color,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "TOP",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "$cantidad productos • $stockTotal unidades",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Indicador de porcentaje
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = color.copy(alpha = 0.1f)
            ) {
                Text(
                    text = "$porcentaje%",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
    }
}

// ============================================
// COMPONENTE: Card de top producto
// ============================================
@Composable
fun TopProductCard(
    nombre: String,
    categoria: String,
    cantidad: Int,
    ranking: Int
) {
    val medalColor = when (ranking) {
        1 -> Color(0xFFFFD700) // Oro
        2 -> Color(0xFFC0C0C0) // Plata
        3 -> Color(0xFFCD7F32) // Bronce
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Medalla de ranking
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = medalColor.copy(alpha = 0.2f)
            ) {
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#$ranking",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (ranking <= 3) medalColor else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Información del producto
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = categoria,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Stock
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "$cantidad",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}