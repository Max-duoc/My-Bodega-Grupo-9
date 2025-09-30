package com.example.mybodega_grupo9.ui.theme.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeMedium(onAdd: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Vista Medium (tablets chicas)")
            // Lista de productos
        }
        Column(modifier = Modifier.weight(1f)) {
            Text("Panel adicional (ej. detalle del producto)")
        }
    }
}
