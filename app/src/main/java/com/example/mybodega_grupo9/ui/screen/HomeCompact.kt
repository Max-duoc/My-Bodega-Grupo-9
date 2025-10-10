package com.example.mybodega_grupo9.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeCompact(
    onNavigateToAdd: () -> Unit,
    onNavigateToDetails: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Bodega") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Bienvenido a tu inventario personal")
            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = onNavigateToAdd) {
                Text("Agregar producto")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = onNavigateToDetails) {
                Text("Ver productos almacenados")
            }
        }
    }
}
