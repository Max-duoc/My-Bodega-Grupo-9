package com.example.mybodega_grupo9.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mybodega_grupo9.R
import kotlinx.coroutines.delay
import com.example.mybodega_grupo9.ui.components.ActionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeExpanded(
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
                        "My Bodega - Inventario Profesional",
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.15f)
                        )
                    )
                )
                .padding(padding)
                .padding(48.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Panel izquierdo: Branding
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Surface(
                    modifier = Modifier.size(200.dp),
                    shape = RoundedCornerShape(32.dp),
                    shadowElevation = 16.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo My Bodega",
                        modifier = Modifier.padding(32.dp)
                    )
                }

                Text(
                    "My Bodega",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    "Sistema de Inventario Inteligente",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    "Organiza • Controla • Optimiza",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            // Panel derecho: Grid de acciones
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ActionCard(
                        title = "Agregar",
                        subtitle = "Nuevo producto",
                        icon = Icons.Default.AddCircle,
                        color = MaterialTheme.colorScheme.primary,
                        onClick = onNavigateToAdd,
                        modifier = Modifier.weight(1f)
                    )
                    ActionCard(
                        title = "Inventario",
                        subtitle = "Ver productos",
                        icon = Icons.Default.Inventory,
                        color = MaterialTheme.colorScheme.secondary,
                        onClick = onNavigateToDetails,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ActionCard(
                        title = "Reportes",
                        subtitle = "Estadísticas",
                        icon = Icons.Default.BarChart,
                        color = MaterialTheme.colorScheme.tertiary,
                        onClick = onNavigateToReport,
                        modifier = Modifier.weight(1f)
                    )
                    ActionCard(
                        title = "Historial",
                        subtitle = "Movimientos",
                        icon = Icons.Default.History,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        onClick = onNavigateToMovimientos,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun ActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
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
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = color
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

