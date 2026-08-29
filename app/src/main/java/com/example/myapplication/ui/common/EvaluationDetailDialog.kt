package com.example.myapplication.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.domain.model.Evaluation
import com.example.myapplication.ui.theme.EcoColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EvaluationDetailDialog(evaluation: Evaluation, roomName: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Detalle de Evaluación: $roomName") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Fecha: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(evaluation.fecha))}",
                    style = MaterialTheme.typography.bodySmall
                )
                
                if (evaluation.evidenciasUrls.isNotEmpty()) {
                    Text("Evidencias:", fontWeight = FontWeight.Bold)
                    androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                        columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(2),
                        modifier = Modifier.height(200.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(evaluation.evidenciasUrls.size) { index ->
                            val url = evaluation.evidenciasUrls[index]
                            AsyncImage(
                                model = url.replace("drive.google.com/open?id=", "lh3.googleusercontent.com/d/"),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                Text("Observaciones:", fontWeight = FontWeight.Bold)
                Text(evaluation.observaciones.ifEmpty { "Sin observaciones." }, style = MaterialTheme.typography.bodyMedium)

                Text("Indicadores:", fontWeight = FontWeight.Bold)
                evaluation.indicadores.forEach { (name, value) ->
                    val unit = if (name.equals("Botellas", ignoreCase = true) || name.equals("Tapas", ignoreCase = true)) "Kg" else "pts"
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(name, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                        Text("$value $unit", fontWeight = FontWeight.Bold, color = EcoColors.DocentePrimary)
                    }
                }

                HorizontalDivider()
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Puntaje Final:", fontWeight = FontWeight.Bold)
                    Text("${evaluation.puntajeObtenido} / 100", fontWeight = FontWeight.ExtraBold, color = EcoColors.DocentePrimary)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        }
    )
}
