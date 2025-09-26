package com.example.mybodega_grupo9.ui.theme


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mybodega_grupo9.viewmodel.MainViewModel


@Composable
fun MainScreen(vm: MainViewModel = viewModel()) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(vm.items) { item ->
            Text("${item.nombre} - ${item.cantidad} (${item.categoria})")
        }
    }
}


