package com.example.mybodega_grupo9.ui.theme


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mybodega_grupo9.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onAddClick: () -> Unit = {}) {
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
            // Logo (coloca logo.png en res/drawable)
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo My Bodega",
                modifier = Modifier
                    .height(120.dp)
                    .width(120.dp)
            )

            // Título / descripción
            Text(text = "Inventario personal de la casa")
            Text(text = "Organiza tus productos por categoría y ubicación")

            // Botón principal
            Button(onClick = onAddClick) {
                Text("Agregar producto")
            }

            // Aquí dejar espacio para lista (se añadirá en siguientes pasos)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Lista de productos aparecerá aquí...")
        }
    }
}



