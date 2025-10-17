package com.example.mybodega_grupo9.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Iniciar Sesión") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            if (error) Text("Credenciales incorrectas", color = MaterialTheme.colorScheme.error)

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (email == "admin@mybodega.cl" && password == "1234") {
                        error = false
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true } // 🔒 evita volver atrás
                        }
                    } else {
                        error = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ingresar")
            }

            TextButton(onClick = { navController.navigate("register") }) {
                Text("¿No tienes cuenta? Regístrate")
            }
        }
    }
}
