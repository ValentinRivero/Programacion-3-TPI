package com.error404.mundialtpi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.error404.mundialtpi.models.DestinoLogin
import com.error404.mundialtpi.viewmodel.AuthState
import com.error404.mundialtpi.viewmodel.AuthViewModel
import com.error404.mundialtpi.utils.Validations

@Composable
fun PantallaRegistro(
    viewModel: AuthViewModel,
    navController: NavController
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    var nombreError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passError by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            viewModel.resetState()
            navController.navigate(DestinoLogin) {
                popUpTo(DestinoLogin) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Registro",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Creá tu cuenta para comprar entradas",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it; nombreError = !Validations.isValidName(it) },
                    label = { Text("Nombre Completo") },
                    isError = nombreError,
                    supportingText = { if (nombreError) Text("Mínimo 3 caracteres") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; emailError = !Validations.isValidEmail(it) },
                    label = { Text("Email") },
                    isError = emailError,
                    supportingText = { if (emailError) Text("Email inválido") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = contrasena,
                    onValueChange = { contrasena = it; passError = !Validations.isValidPassword(it) },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = passError,
                    supportingText = { if (passError) Text("Mínimo 4 caracteres") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.registro(nombre, email, contrasena) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    enabled = authState !is AuthState.Loading && Validations.isValidName(nombre) && Validations.isValidEmail(email) && Validations.isValidPassword(contrasena)
                ) {
                    if (authState is AuthState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Registrar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (authState is AuthState.Error) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = (authState as AuthState.Error).message, color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = {
                    viewModel.resetState()
                    navController.popBackStack()
                }) {
                    Text("Volver al Login", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}