package com.example.mybodega_grupo9.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mybodega_grupo9.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeCompact(
    onNavigateToAdd: () -> Unit,
    onNavigateToDetails: () -> Unit,
    onNavigateToReport: () -> Unit,
    onNavigateToMovimientos: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Bodega") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo My Bodega",
                modifier = Modifier
                    .height(120.dp)
                    .width(120.dp)
            )

            Text(text = "Inventario personal de la casa")
            Text(text = "Organiza tus productos por categoría y ubicación")

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = onNavigateToAdd) {
                Text("Agregar producto")
            }

            Button(onClick = onNavigateToDetails) {
                Text("Ver productos almacenados")
            }

            // Nuevo botón Reportes
            Button(onClick = onNavigateToReport) {
                Text("Reportes")
            }

            Button(onClick = onNavigateToMovimientos, modifier = Modifier.fillMaxWidth()) {
                Text("Ver historial de movimientos")
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text("Lista de productos aparecerá aquí...")
        }
    }
}
