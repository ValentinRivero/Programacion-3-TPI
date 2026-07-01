package com.error404.mundialtpi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.error404.mundialtpi.models.DestinoMisTickets
import com.error404.mundialtpi.ui.components.InfoRow
import com.error404.mundialtpi.ui.components.TeamDetailItem
import com.error404.mundialtpi.ui.theme.FifaBlue
import com.error404.mundialtpi.ui.theme.FifaOrange
import com.error404.mundialtpi.utils.formatDateTime
import com.error404.mundialtpi.viewmodel.MundialViewModel
import com.error404.mundialtpi.viewmodel.TicketState
import com.error404.mundialtpi.viewmodel.TicketViewModel

// Definimos la estructura como en tu web (ID, Nombre, Precio)
data class OpcionEntrada(val id: Int, val nombre: String, val precio: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalle(
    partidoId: Int,
    viewModel: MundialViewModel,
    ticketViewModel: TicketViewModel,
    navController: NavController,
    onBack: () -> Unit
) {
    var cantidad by remember { mutableStateOf(1) }

    // Armamos la lista con los IDs reales de la base de datos
    val opcionesEntradas = listOf(
        OpcionEntrada(1, "Categoría 1 - Laterales", 250),
        OpcionEntrada(2, "Categoría 2 - Córners", 150),
        OpcionEntrada(3, "Categoría 3 - Cabeceras", 100)
    )

    var categoriaSeleccionada by remember { mutableStateOf(opcionesEntradas[0]) }
    var expanded by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    val ticketState by ticketViewModel.ticketState.collectAsState()

    LaunchedEffect(partidoId) {
        viewModel.cargarPartidoDetalle(partidoId)
    }

    LaunchedEffect(ticketState) {
        when (ticketState) {
            is TicketState.Success -> {
                dialogMessage = "¡Compra confirmada! Adquiriste $cantidad entrada(s) para el sector ${categoriaSeleccionada.nombre.split(" -")[0]}. Total: $${categoriaSeleccionada.precio * cantidad} USD."
                showDialog = true
            }
            is TicketState.Error -> {
                dialogMessage = "Error: ${(ticketState as TicketState.Error).message}"
                showDialog = true
            }
            else -> {}
        }
    }

    val partido = viewModel.partidoSeleccionado

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comprar Entrada", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (viewModel.isLoading || partido == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // SECCIÓN 1: CABECERA DEL PARTIDO
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
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

                            Spacer(modifier = Modifier.height(16.dp))

                            val (date, time) = formatDateTime(partido.fecha)
                            InfoRow(label = "Estadio", value = partido.nombreEstadio)
                            InfoRow(label = "Fecha", value = "$date - $time hs")
                            InfoRow(label = "Fase", value = partido.fase)
                            InfoRow(label = "Disponibilidad", value = partido.infoEntradas)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // SECCIÓN 2: FORMULARIO DE COMPRA
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Seleccioná tus lugares",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            Text("Categoría de entrada", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded }
                            ) {
                                OutlinedTextField(
                                    value = categoriaSeleccionada.nombre,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                    modifier = Modifier.menuAnchor().fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    opcionesEntradas.forEach { opcion ->
                                        DropdownMenuItem(
                                            text = { Text("${opcion.nombre} ($${opcion.precio})") },
                                            onClick = {
                                                categoriaSeleccionada = opcion
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text("Cantidad de tickets (Máximo 4)", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                IconButton(
                                    onClick = { if (cantidad > 1) cantidad-- },
                                    colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) { Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold) }

                                Text(
                                    text = "$cantidad",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )

                                IconButton(
                                    onClick = { if (cantidad < 4) cantidad++ },
                                    colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) { Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Total estimado:", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                                Text("$${categoriaSeleccionada.precio * cantidad} USD", fontSize = 24.sp, fontWeight = FontWeight.Black, color = FifaOrange)
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = {
                                    // MANDAMOS EL ID NUMÉRICO (1, 2 o 3)
                                    ticketViewModel.comprarTicket(partidoId, categoriaSeleccionada.id, cantidad)
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = MaterialTheme.shapes.medium,
                                colors = ButtonDefaults.buttonColors(containerColor = FifaOrange),
                                enabled = ticketState !is TicketState.Loading && partido.entradasDisponibles > 0
                            ) {
                                if (ticketState is TicketState.Loading) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                                } else {
                                    Text("CONFIRMAR PAGO", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                ticketViewModel.resetState()
            },
            title = { Text("Estado de Compra") },
            text = { Text(dialogMessage) },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    ticketViewModel.resetState()
                    if (ticketState is TicketState.Success) {
                        navController.navigate(com.error404.mundialtpi.models.DestinoMisTickets) {
                            // Borramos la pantalla de compra, pero mantenemos viva la Lista principal
                            popUpTo(com.error404.mundialtpi.models.DestinoLista) { inclusive = false }
                        }
                    }
                }) {
                    Text("Aceptar")
                }
            }
        )
    }
}