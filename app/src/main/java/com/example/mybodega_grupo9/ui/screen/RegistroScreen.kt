package com.example.mybodega_grupo9.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mybodega_grupo9.viewmodel.UsuarioViewModel

@Composable
fun RegistroScreen(
    navController: NavController,
    viewModel: UsuarioViewModel
) {
    val estado by viewModel.estado.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 16.dp),
    verticalArrangement = Arrangement.spacedBy(space = 12.dp)
    ) {
        // Campo nombre [cite: 206]
        OutlinedTextField(
            value = estado.nombre,
        onValueChange = viewModel::onNombreChange,
        label = { Text("Nombre") },
        isError = estado.errores.nombre != null,
        supportingText = {
            estado.errores.nombre?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        },
        modifier = Modifier.fillMaxWidth()
        )

        // Campo correo [cite: 227]
        OutlinedTextField(
            value = estado.correo,
        onValueChange = viewModel::onCorreoChange,
        label = { Text("Correo electrónico") },
        isError = estado.errores.correo != null,
        supportingText = {
            estado.errores.correo?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        },
        modifier = Modifier.fillMaxWidth()
        )

        // Campo clave [cite: 240]
        OutlinedTextField(
            value = estado.clave,
        onValueChange = viewModel::onClaveChange,
        label = { Text("Contraseña") },
        visualTransformation = PasswordVisualTransformation(),
        isError = estado.errores.clave != null,
        supportingText = {
            estado.errores.clave?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        },
        modifier = Modifier.fillMaxWidth()
        )

        // Campo dirección [cite: 260]
        OutlinedTextField(
            value = estado.direccion,
        onValueChange = viewModel::onDireccionChange,
        label = { Text("Dirección") },
        isError = estado.errores.direccion != null,
        supportingText = {
            estado.errores.direccion?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        },
        modifier = Modifier.fillMaxWidth()
        )

        // Checkbox: aceptar términos [cite: 273]
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = estado.aceptaTerminos,
            onCheckedChange = viewModel::onAceptanTerminosChange
            )
            Spacer(Modifier.width(8.dp))
            Text("Acepto los términos y condiciones")
        }

        // Botón: enviar [cite: 282]
        Button(
            onClick = {
                // Si el formulario es válido, navega [cite: 255]
                if (viewModel.validarFormulario()) {
                    navController.navigate("resumen")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar")
        }
    }
}