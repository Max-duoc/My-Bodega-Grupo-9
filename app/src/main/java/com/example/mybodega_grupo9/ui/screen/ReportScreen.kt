package com.example.mybodega_grupo9.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mybodega_grupo9.viewmodel.ProductoViewModel
import com.example.mybodega_grupo9.viewmodel.MovimientoViewModel
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    vm: ProductoViewModel = viewModel(),
    movimientoVm: MovimientoViewModel = viewModel()
) {
    val productos by vm.productos.collectAsState()
    val movimientos by movimientoVm.movimientos.collectAsState()

    // --- 1️⃣ Productos más consumidos ---
    val consumoPorProducto = movimientos
        .filter { it.tipo == "Consumo" }
        .groupingBy { it.producto }
        .eachCount()
        .toList()
        .sortedByDescending { it.second }
        .take(5)

    // --- 2️⃣ Productos por categoría ---
    val productosPorCategoria = productos
        .groupingBy { it.categoria }
        .eachCount()

    // --- 3️⃣ Alertas de stock bajo ---
    val alertasStock = productos.filter { it.cantidad <= 2 }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Reportes de Inventario") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // --- Sección 1: Consumo ---
            item {
                Text(
                    "📊 Productos más consumidos",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                if (consumoPorProducto.isEmpty()) {
                    Text("Aún no hay datos de consumo registrados.")
                } else {
                    BarChart(consumoPorProducto)
                }
            }

            // --- Sección 2: Categorías ---
            item {
                Text(
                    "🧩 Registros por categoría",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                if (productosPorCategoria.isEmpty()) {
                    Text("No hay productos registrados aún.")
                } else {
                    PieChartSafe(productosPorCategoria)
                }
            }

            // --- Sección 3: Alertas ---
            item {
                Text(
                    "⚠️ Alertas de stock bajo",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                AnimatedVisibility(visible = alertasStock.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        alertasStock.forEach {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    Text("Producto: ${it.nombre}", fontWeight = FontWeight.Bold)
                                    Text("Cantidad: ${it.cantidad}")
                                    Text("Categoría: ${it.categoria}")
                                }
                            }
                        }
                    }
                }

                if (alertasStock.isEmpty()) {
                    Text("No hay alertas activas 🎉")
                }
            }
        }
    }
}

@Composable
fun BarChart(data: List<Pair<String, Int>>) {
    val max = max(data.maxOfOrNull { it.second } ?: 1, 1)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        data.forEach { (nombre, valor) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    nombre,
                    modifier = Modifier.width(120.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
                LinearProgressIndicator(
                    progress = valor.toFloat() / max,
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                Text(valor.toString())
            }
        }
    }
}

@Composable
fun PieChartSafe(data: Map<String, Int>) {
    val total = data.values.sum().toFloat().takeIf { it > 0 } ?: 1f
    val colors = listOf(
        Color(0xFFEF5350),
        Color(0xFF66BB6A),
        Color(0xFF42A5F5),
        Color(0xFFFFCA28),
        Color(0xFFAB47BC)
    )
    val proportions = data.values.map { it / total }
    val categories = data.keys.toList()

    Box(
        Modifier
            .fillMaxWidth()
            .height(220.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.size(180.dp)) {
            var startAngle = -90f
            proportions.forEachIndexed { index, proportion ->
                val sweep = proportion * 360f
                drawArc(
                    color = colors[index % colors.size],
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = true
                )
                startAngle += sweep
            }
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        categories.forEachIndexed { index, cat ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(colors[index % colors.size])
                )
                Text(cat)
            }
        }
    }
}
