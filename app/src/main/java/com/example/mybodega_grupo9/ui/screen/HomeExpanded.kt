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
fun HomeExpanded(
    onNavigateToAdd: () -> Unit,
    onNavigateToDetails: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Bodega") })
        }
    ) { padding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(32.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Izquierda: logo e información
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo My Bodega",
                    modifier = Modifier
                        .height(180.dp)
                        .width(180.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Inventario personal de la casa", style = MaterialTheme.typography.titleLarge)
                Text("Organiza tus productos por categoría y ubicación")
            }

            // Derecha: botones grandes
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Button(onClick = onNavigateToAdd, modifier = Modifier.width(240.dp)) {
                    Text("Agregar producto")
                }
                Button(onClick = onNavigateToDetails, modifier = Modifier.width(240.dp)) {
                    Text("Ver productos almacenados")
                }
            }
        }
    }
}

