package com.commityarezar.mundialtpi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.commityarezar.mundialtpi.viewmodel.MundialViewModel

@Composable
fun PantallaDetalle(partidoId: String, viewModel: MundialViewModel) {

    // LaunchedEffect se ejecuta una sola vez cuando la pantalla se abre
    LaunchedEffect(partidoId) {
        viewModel.cargarPartidoDetalle(partidoId)
    }

    // Obtenemos el partido del ViewModel
    val partido = viewModel.partidoSeleccionado

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mostramos el loader si está cargando o si el partido todavía es null
        if (viewModel.isLoading || partido == null) {
            Spacer(modifier = Modifier.height(100.dp))
            CircularProgressIndicator()
        } else {
            // Cuando ya tenemos los datos, los dibujamos
            Text("Detalles del Partido", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Encuentro: ${partido.equipo1} vs ${partido.equipo2}", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Grupo: ${partido.grupo}", style = MaterialTheme.typography.bodyLarge)
                    Text("Fecha: ${partido.fecha}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Estadio: ${partido.estadio}", style = MaterialTheme.typography.titleMedium)
                    Text("Precio de entrada: ${partido.precio}", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}