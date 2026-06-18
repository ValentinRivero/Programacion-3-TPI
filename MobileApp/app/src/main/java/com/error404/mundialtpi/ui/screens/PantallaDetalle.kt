package com.error404.mundialtpi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.error404.mundialtpi.ui.components.InfoRow
import com.error404.mundialtpi.ui.components.TeamDetailItem
import com.error404.mundialtpi.utils.formatDateTime
import com.error404.mundialtpi.viewmodel.MundialViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalle(partidoId: Int, viewModel: MundialViewModel, navController: NavController, onBack: () -> Unit) {

    // Cargamos el detalle al entrar
    LaunchedEffect(partidoId) {
        viewModel.cargarPartidoDetalle(partidoId)
    }

    val partido = viewModel.partidoSeleccionado

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Encuentro", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (viewModel.isLoading || partido == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Tarjeta de Enfrentamiento Principal
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TeamDetailItem(name = partido.equipo1, flagUrl = partido.flag1, modifier = Modifier.weight(1f))

                            Text(
                                text = "VS",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            TeamDetailItem(name = partido.equipo2, flagUrl = partido.flag2, modifier = Modifier.weight(1f), isEnd = true)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Información Detallada
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Información del Partido",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            InfoRow(label = "Estadio", value = partido.nombreEstadio)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

                            val (date, time) = formatDateTime(partido.fecha)
                            InfoRow(label = "Fecha", value = date)
                            InfoRow(label = "Hora", value = "$time hs")

                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)
                            InfoRow(label = "Fase", value = partido.fase) // Acá ponemos Fase

                            Spacer(modifier = Modifier.height(24.dp))

                            // Sección de Precio
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Disponibilidad",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        partido.infoEntradas,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Button(
                                onClick = { navController.navigate(com.error404.mundialtpi.models.DestinoCompra(partidoId)) },
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                Text("Comprar Entrada", style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}