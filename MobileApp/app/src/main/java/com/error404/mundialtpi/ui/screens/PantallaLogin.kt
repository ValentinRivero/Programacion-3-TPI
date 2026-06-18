package com.error404.mundialtpi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.error404.mundialtpi.models.DestinoLista
import com.error404.mundialtpi.models.DestinoRegistro
import com.error404.mundialtpi.viewmodel.AuthState
import com.error404.mundialtpi.viewmodel.AuthViewModel
import com.error404.mundialtpi.utils.Validations

@Composable
fun PantallaLogin(
    viewModel: AuthViewModel,
    navController: NavController
) {
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf(false) }
    var passError by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            viewModel.resetState()
            navController.navigate(DestinoLista) {
                popUpTo(0)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "🏆 FIFA 2026", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = !Validations.isValidEmail(it)
            },
            label = { Text("Email") },
            isError = emailError,
            supportingText = { if (emailError) Text("Email inválido") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = {
                contrasena = it
                passError = !Validations.isValidPassword(it)
            },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            isError = passError,
            supportingText = { if (passError) Text("Mínimo 4 caracteres") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.login(email, contrasena) },
            modifier = Modifier.fillMaxWidth(),
            enabled = authState !is AuthState.Loading &&
                    Validations.isValidEmail(email) &&
                    Validations.isValidPassword(contrasena)
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Iniciar Sesión")
            }
        }

        if (authState is AuthState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = (authState as AuthState.Error).message, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = {
            viewModel.resetState()
            navController.navigate(DestinoRegistro)
        }) {
            Text("¿No tenés cuenta? Registrate aquí")
        }
    }
}