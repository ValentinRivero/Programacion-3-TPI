package com.commityarezar.mundialtpi.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.commityarezar.mundialtpi.models.DestinoDetalle
import com.commityarezar.mundialtpi.viewmodel.MundialViewModel

@Composable
fun PantallaLista(viewModel: MundialViewModel, navController: NavController) {
    // Esto asegura que se carguen los partidos cuando se abre la pantalla
    LaunchedEffect(Unit) {
        viewModel.cargarPartidos()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Fase de Grupos", style = MaterialTheme.typography.headlineMedium)

        if (viewModel.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }

        LazyColumn(modifier = Modifier.weight(1f).padding(top = 16.dp)) {
            items(viewModel.partidosLista) { partido ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            navController.navigate(DestinoDetalle(partido.id))
                        }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("${partido.equipo1} vs ${partido.equipo2}", style = MaterialTheme.typography.titleLarge)
                        Text("Grupo: ${partido.grupo} | Fecha: ${partido.fecha}")
                    }
                }
            }
        }
    }
}