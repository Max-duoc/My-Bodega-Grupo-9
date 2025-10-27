package com.example.mybodega_grupo9.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mybodega_grupo9.R
import kotlinx.coroutines.delay
import com.example.mybodega_grupo9.ui.components.ActionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeMedium(
    onNavigateToAdd: () -> Unit,
    onNavigateToDetails: () -> Unit,
    onNavigateToReport: () -> Unit,
    onNavigateToMovimientos: () -> Unit
) {
    var logoVisible by remember { mutableStateOf(false) }

    // Animación de aparición del logo
    LaunchedEffect(Unit) {
        delay(300)
        logoVisible = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Bodega",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Top),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 🔹 Logo animado
                AnimatedVisibility(
                    visible = logoVisible,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo My Bodega",
                        modifier = Modifier
                            .height(130.dp)
                            .width(130.dp)
                    )
                }

                Text(
                    text = "Tu inventario personal de hogar",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
                )

                Spacer(Modifier.height(16.dp))

                // 🟦 Cards principales (botones visuales)
                ActionCard(
                    title = "Agregar Producto",
                    icon = Icons.Default.Add,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    onClick = onNavigateToAdd
                )

                ActionCard(
                    title = "Ver Inventario",
                    icon = Icons.Default.Inventory,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = onNavigateToDetails
                )

                ActionCard(
                    title = "Ver Reportes",
                    icon = Icons.Default.BarChart,
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    onClick = onNavigateToReport
                )

                ActionCard(
                    title = "Historial de Movimientos",
                    icon = Icons.Default.History,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    onClick = onNavigateToMovimientos
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    "Administra, controla y visualiza tu bodega fácilmente.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }
    }
}