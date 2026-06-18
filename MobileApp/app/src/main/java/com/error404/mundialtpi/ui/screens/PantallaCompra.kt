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
import com.error404.mundialtpi.models.DestinoLista
import com.error404.mundialtpi.viewmodel.TicketState
import com.error404.mundialtpi.viewmodel.TicketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCompra(
    partidoId: Int,
    viewModel: TicketViewModel,
    navController: NavController
) {
    val idInt = partidoId
    var tipoEntrada by remember { mutableStateOf("General") }
    var cantidad by remember { mutableStateOf(1) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    
    val ticketState by viewModel.ticketState.collectAsState()

    LaunchedEffect(ticketState) {
        when (ticketState) {
            is TicketState.Success -> {
                dialogMessage = "¡Compra confirmada! Adquiriste $cantidad entrada(s) para el sector $tipoEntrada. Ya podés verla(s) en Mis Entradas."
                showDialog = true
            }
            is TicketState.Error -> {
                dialogMessage = "Error: ${(ticketState as TicketState.Error).message}"
                showDialog = true
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comprar Entrada") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Seleccione el Tipo de Entrada", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("General", "Platea", "VIP").forEach { tipo ->
                    FilterChip(
                        selected = tipoEntrada == tipo,
                        onClick = { tipoEntrada = tipo },
                        label = { Text(tipo) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Cantidad", style = MaterialTheme.typography.titleMedium)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                IconButton(onClick = { if (cantidad > 1) cantidad-- }) {
                    Text("-", style = MaterialTheme.typography.headlineMedium)
                }
                Text(text = "$cantidad", style = MaterialTheme.typography.headlineMedium)
                IconButton(onClick = { if (cantidad < 10) cantidad++ }) {
                    Text("+", style = MaterialTheme.typography.headlineMedium)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.comprarTicket(idInt, tipoEntrada, cantidad) },
                modifier = Modifier.fillMaxWidth(),
                enabled = ticketState !is TicketState.Loading
            ) {
                if (ticketState is TicketState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Confirmar Compra")
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { 
                showDialog = false
                viewModel.resetState()
            },
            title = { Text("Información") },
            text = { Text(dialogMessage) },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    viewModel.resetState()
                    if (ticketState is TicketState.Success) {
                        navController.navigate(DestinoLista) {
                            popUpTo(0)
                        }
                    }
                }) {
                    Text("Aceptar")
                }
            }
        )
    }
}
