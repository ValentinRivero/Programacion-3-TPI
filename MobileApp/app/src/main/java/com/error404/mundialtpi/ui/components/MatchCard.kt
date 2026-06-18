package com.error404.mundialtpi.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.error404.mundialtpi.models.DTOPartidosLista
import com.error404.mundialtpi.utils.formatDateTime

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
                val (date, time) = formatDateTime(partido.fecha)
                val formattedDate = "$date - $time hs"

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