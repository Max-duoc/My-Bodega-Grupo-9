package com.example.mybodega_grupo9.ui.screen


import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeExpanded(onAdd: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Vista Expanded (pantallas grandes)")
            // Lista de productos
        }
        Column(modifier = Modifier.weight(2f)) {
            Text("Panel central (detalle)")
        }
        Column(modifier = Modifier.weight(1f)) {
            Text("Panel lateral (acciones rápidas)")
        }
    }
    HomeScreen()
}
