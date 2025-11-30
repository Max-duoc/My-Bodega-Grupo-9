// ============================================
// MovimientosScreen.kt - VERSIÓN MEJORADA
// ============================================
package com.example.mybodega_grupo9.ui.screen

import android.text.format.DateFormat
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import com.example.mybodega_grupo9.data.local.MovimientoEntity
import com.example.mybodega_grupo9.viewmodel.MovimientoViewModel
import java.sql.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovimientosScreen(vm: MovimientoViewModel = viewModel()) {
    val movimientos by vm.movimientos.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var filterType by remember { mutableStateOf<String?>(null) }

    // ⬇️ AGREGAR ESTA SINCRONIZACIÓN AL ABRIR LA PANTALLA
    LaunchedEffect(Unit) {
        vm.syncMovimientos()
    }

    // ... resto del código sin cambios ...


    // Filtrado de movimientos
    val movimientosFiltrados = if (filterType != null) {
        movimientos.filter { it.tipo == filterType }
    } else {
        movimientos
    }

    // Estadísticas
    val totalMovimientos = movimientos.size
    val movimientosPorTipo = movimientos.groupBy { it.tipo }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Historial de Movimientos",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                        Text(
                            "$totalMovimientos registros",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                },
                actions = {
                    if (movimientos.isNotEmpty()) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Default.DeleteSweep,
                                contentDescription = "Limpiar historial",
                                tint = MaterialTheme.colorScheme.error
                            )
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
            // Dashboard de tipos de movimientos
            if (movimientos.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        FilterChip(
                            title = "Todos",
                            count = totalMovimientos,
                            isSelected = filterType == null,
                            onClick = { filterType = null }
                        )
                    }

                    items(movimientosPorTipo.entries.toList()) { (tipo, lista) ->
                        FilterChip(
                            title = tipo,
                            count = lista.size,
                            isSelected = filterType == tipo,
                            onClick = { filterType = if (filterType == tipo) null else tipo }
                        )
                    }
                }
            }

            // Lista de movimientos
            if (movimientosFiltrados.isEmpty()) {
                EmptyMovimientosState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    hasFilter = filterType != null
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(movimientosFiltrados, key = { it.id }) { movimiento ->
                        MovimientoCard(movimiento = movimiento)
                    }
                }
            }
        }
    }

    // Diálogo de confirmación para limpiar historial
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text("Limpiar historial")
                }
            },
            text = {
                Text("¿Estás seguro de que deseas eliminar todos los registros del historial? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        vm.clearAll()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar todo")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

// ============================================
// COMPONENTE: Chip de filtro
// ============================================
@Composable
fun FilterChip(
    title: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = count.toString(),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        leadingIcon = if (isSelected) {
            {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        } else null,
        shape = RoundedCornerShape(12.dp)
    )
}

// ============================================
// COMPONENTE: Card de movimiento
// ============================================
@Composable
fun MovimientoCard(movimiento: MovimientoEntity) {
    val (icon, color) = getTipoInfo(movimiento.tipo)
    val fecha = DateFormat.format("dd/MM/yyyy", Date(movimiento.fecha)).toString()
    val hora = DateFormat.format("HH:mm", Date(movimiento.fecha)).toString()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono del tipo de movimiento
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

            // Información del movimiento
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = color.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = movimiento.tipo,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = color,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = movimiento.producto,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = fecha,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = hora,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// ============================================
// COMPONENTE: Estado vacío
// ============================================
@Composable
fun EmptyMovimientosState(
    modifier: Modifier = Modifier,
    hasFilter: Boolean
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            if (hasFilter) Icons.Default.FilterAlt else Icons.Default.History,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = if (hasFilter) "No hay movimientos de este tipo" else "Sin movimientos registrados",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = if (hasFilter) "Prueba con otro filtro" else "Los cambios aparecerán aquí",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}

// ============================================
// FUNCIÓN: Obtener info del tipo de movimiento
// ============================================
fun getTipoInfo(tipo: String): Pair<ImageVector, Color> {
    return when (tipo) {
        "Agregar" -> Pair(Icons.Default.AddCircle, Color(0xFF10B981))
        "Editar" -> Pair(Icons.Default.Edit, Color(0xFF3B82F6))
        "Eliminar" -> Pair(Icons.Default.Delete, Color(0xFFEF4444))
        "Consumo" -> Pair(Icons.Default.Remove, Color(0xFFF59E0B))
        "Reabastecimiento" -> Pair(Icons.Default.Add, Color(0xFF8B5CF6))
        else -> Pair(Icons.Default.ChangeCircle, Color(0xFF6B7280))
    }


}