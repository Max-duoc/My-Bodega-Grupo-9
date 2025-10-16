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
fun HomeMedium(
    onNavigateToAdd: () -> Unit,
    onNavigateToDetails: () -> Unit,
    onNavigateToReport: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Bodega") }
            )
        }
    ) { padding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo My Bodega",
                    modifier = Modifier
                        .height(140.dp)
                        .width(140.dp)
                )
                Text("Inventario personal de la casa")
                Text("Organiza tus productos por categoría y ubicación")
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Button(onClick = onNavigateToAdd, modifier = Modifier.width(200.dp)) {
                    Text("Agregar producto")
                }
                Button(onClick = onNavigateToDetails, modifier = Modifier.width(200.dp)) {
                    Text("Ver productos almacenados")
                }
                Button(onClick = onNavigateToReport, modifier = Modifier.width(200.dp)) {
                    Text("Reportes")
                }
            }
        }
    }
}


