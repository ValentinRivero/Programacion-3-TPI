package com.commityarezar.mundialtpi.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.commityarezar.mundialtpi.models.DTOPartidosLista
import com.commityarezar.mundialtpi.models.DestinoDetalle
import com.commityarezar.mundialtpi.viewmodel.MundialViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaLista(viewModel: MundialViewModel, navController: NavController) {
    LaunchedEffect(Unit) {
        viewModel.cargarPartidos()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mundial TPI", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.toggleTheme() }) {
                        Icon(
                            imageVector = if (viewModel.isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Cambiar modo"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
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

@Composable
fun MatchCard(partido: DTOPartidosLista, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TeamSection(name = partido.equipo1, flagUrl = partido.flag1, modifier = Modifier.weight(1f))
                
                Text(
                    text = "VS",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                TeamSection(name = partido.equipo2, flagUrl = partido.flag2, modifier = Modifier.weight(1f), isEnd = true)
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val formattedDate = formatMatchDate(partido.fecha)
                Text(
                    text = "Grupo ${partido.grupo}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

fun formatMatchDate(rawDate: String): String {
    return try {
        val instant = Instant.parse(rawDate)
        val dateTime = instant.atZone(ZoneId.systemDefault())
        val datePart = dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val timePart = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        "$datePart - $timePart hs"
    } catch (e: Exception) {
        rawDate
    }
}

@Composable
fun TeamSection(name: String, flagUrl: String, modifier: Modifier = Modifier, isEnd: Boolean = false) {
    Column(
        modifier = modifier,
        horizontalAlignment = if (isEnd) Alignment.End else Alignment.Start
    ) {
        AsyncImage(
            model = flagUrl,
            contentDescription = "Bandera de $name",
            modifier = Modifier
                .size(40.dp)
                .padding(bottom = 4.dp),
        )
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = if (isEnd) TextAlign.End else TextAlign.Start,
            maxLines = 1
        )
    }
}