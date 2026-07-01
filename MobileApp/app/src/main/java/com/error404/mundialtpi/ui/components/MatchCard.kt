package com.error404.mundialtpi.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.error404.mundialtpi.models.DTOPartidosLista
import com.error404.mundialtpi.utils.formatDateTime

@Composable
fun MatchCard(partido: DTOPartidosLista, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            Box(
                modifier = Modifier.fillMaxWidth().height(120.dp)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = partido.flag1,
                        contentDescription = null,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center // Mantiene el centro de la bandera
                    )
                    AsyncImage(
                        model = partido.flag2,
                        contentDescription = null,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .background(Color(0xFF2C3E50).copy(alpha = 0.9f), RoundedCornerShape(50))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = partido.fase.uppercase(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "VS",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                }
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${partido.equipo1} vs ${partido.equipo2}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary, // Usa tu azul del CSS
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                val (date, time) = formatDateTime(partido.fecha)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "📅 $date",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "🕒 $time hs",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary) // Usa tu naranja del CSS
                ) {
                    Text("Ver detalles", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}