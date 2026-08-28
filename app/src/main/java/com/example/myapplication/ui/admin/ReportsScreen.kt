package com.example.myapplication.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.ui.theme.EcoColors
import com.example.myapplication.ui.viewmodel.ReportsViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    onBack: () -> Unit,
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val avgScores = viewModel.averageScoreByCourse.collectAsState()
    val courses = viewModel.courses.collectAsState()
    val rooms = viewModel.rooms.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportes y Estadísticas", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = EcoColors.TextDark
                )
            )
        },
        containerColor = EcoColors.AdminBackground
    ) { padding ->
        val evaluations = viewModel.evaluations.collectAsState()
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Revisiones de Docentes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EcoColors.TextDark
                )
            }

            items(evaluations.value.filter { it.puntajeObtenido == 0 }.sortedByDescending { it.fecha }) { evaluation ->
                val roomName = rooms.value.find { it.id == evaluation.roomId }?.nombre ?: evaluation.roomId
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Revisión de: $roomName", fontWeight = FontWeight.Bold)
                        Text("Fecha: ${java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(java.util.Date(evaluation.fecha))}", style = MaterialTheme.typography.bodySmall)
                        if (evaluation.observaciones.isNotEmpty()) {
                            Text("Obs: ${evaluation.observaciones}", style = MaterialTheme.typography.bodySmall, color = EcoColors.TextMuted)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            (1..5).forEach { score ->
                                Button(
                                    onClick = { viewModel.rateEvaluation(evaluation.id, score * 20) },
                                    colors = ButtonDefaults.buttonColors(containerColor = EcoColors.AdminPrimary),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("$score", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    "Resumen de Desempeño",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EcoColors.TextDark
                )
            }

            item {
                ReportActionCard(
                    title = "Reporte Semanal de Limpieza",
                    description = "PDF generado con el ranking y promedios de la semana actual.",
                    icon = Icons.Default.Download,
                    onClick = { /* Descargar */ }
                )
            }

            item {
                ReportActionCard(
                    title = "Tendencias de Consumo",
                    description = "Análisis visual de los indicadores de ahorro de energía y agua.",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    onClick = { /* Ver tendencias */ }
                )
            }

            item {
                ReportActionCard(
                    title = "Participación por Salón",
                    description = "Estadísticas detalladas de la involucración de los estudiantes.",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    onClick = { /* Ver participación */ }
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Puntajes Promedio por Salón/Aula",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EcoColors.TextDark
                )
            }

            val courseList = courses.value.map { it.id to it.nombre }
            val roomList = rooms.value.map { it.id to it.nombre }
            val allItems = (courseList + roomList).distinctBy { it.first }

            items(allItems) { pair ->
                val id = pair.first
                val nombre = pair.second
                val avg = avgScores.value[id] ?: 0.0
                if (avgScores.value.containsKey(id)) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(nombre, fontWeight = FontWeight.Bold)
                            Text(String.format(Locale.getDefault(), "%.2f", avg), color = EcoColors.AdminPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportActionCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(EcoColors.AdminPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = EcoColors.AdminPrimary)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Text(description, style = MaterialTheme.typography.bodySmall, color = EcoColors.TextMuted)
            }
        }
    }
}
