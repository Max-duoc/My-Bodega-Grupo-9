package com.example.mybodega_grupo9.ui.anim

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mybodega_grupo9.viewmodel.ModeViewModel

@Composable
fun ModeScreen(vm: ModeViewModel = viewModel()) {
    val estado = vm.enabled.collectAsState()

    // Mostrar loader mientras carga
    if (estado.value == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        // Estados derivados y animaciones
        val text by remember(estado.value) {
            derivedStateOf { if (estado.value == true) "Modo activado" else "Modo desactivado" }
        }

        val color by animateColorAsState(
            targetValue = if (estado.value == true) Color(0xFF4CAF50) else Color(0xFF9E9E9E)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { vm.toggleModo() },
                colors = ButtonDefaults.buttonColors(containerColor = color)
            ) {
                Text(text)
            }

            Spacer(modifier = Modifier.height(20.dp))

            AnimatedVisibility(visible = estado.value == true) {
                Text("¡Modo especial activo!", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
