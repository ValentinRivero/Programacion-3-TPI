package com.error404.mundialtpi.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun TeamDetailItem(name: String, flagUrl: String, modifier: Modifier = Modifier, isEnd: Boolean = false) {
    Column(
        modifier = modifier,
        horizontalAlignment = if (isEnd) Alignment.End else Alignment.Start
    ) {
        AsyncImage(
            model = flagUrl,
            contentDescription = "Bandera de $name",
            modifier = Modifier
                .size(64.dp)
                .padding(bottom = 8.dp)
        )
        Text(
            text = name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = if (isEnd) TextAlign.End else TextAlign.Start,
            maxLines = 2
        )
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}