package com.example.mybodega_grupo9.ui.screen

import android.text.format.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentDataType.Companion.Date
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mybodega_grupo9.viewmodel.MovimientoViewModel
import java.sql.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovimientosScreen(vm: MovimientoViewModel = viewModel()) {
    val movimientos by vm.movimientos.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Historial de movimientos") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(movimientos) { mov ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        val fecha = DateFormat.format("dd/MM/yyyy HH:mm", Date(mov.fecha)).toString()
                        Text("$fecha — ${mov.tipo}", style = MaterialTheme.typography.titleMedium)
                        Text("Producto: ${mov.producto}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

