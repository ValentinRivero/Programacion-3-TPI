package com.error404.mundialtpi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.error404.mundialtpi.models.DestinoDetalle
import com.error404.mundialtpi.ui.components.MatchCard
import com.error404.mundialtpi.viewmodel.MundialViewModel
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import com.error404.mundialtpi.models.DestinoLogin
import com.error404.mundialtpi.viewmodel.AuthViewModel
import androidx.compose.material.icons.filled.ConfirmationNumber
import com.error404.mundialtpi.models.DestinoMisTickets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaLista(
    viewModel: MundialViewModel,
    authViewModel: AuthViewModel,
    navController: NavController
) {
    LaunchedEffect(Unit) {
        viewModel.cargarPartidos()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Comprar Entradas", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate(DestinoMisTickets)
                    }) {
                        Icon(
                            imageVector = Icons.Default.ConfirmationNumber,
                            contentDescription = "Mis Entradas"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleTheme() }) {
                        Icon(
                            imageVector = if (viewModel.isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Cambiar modo"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hola, ${authViewModel.nombreUsuario}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = {
                            authViewModel.logout()
                            viewModel.limpiarDetalle()

                            navController.navigate(DestinoLogin) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Cerrar sesión",
                            modifier = Modifier.padding(end = 8.dp).size(18.dp)
                        )
                        Text("Salir")
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            "Partidos Disponibles",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(viewModel.partidosLista) { partido ->
                        MatchCard(partido = partido) {
                            navController.navigate(DestinoDetalle(partido.id))
                        }
                    }
                }
            }
        }
    }
}