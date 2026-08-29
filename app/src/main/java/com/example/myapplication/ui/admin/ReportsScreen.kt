package com.example.myapplication.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.myapplication.domain.model.Evaluation
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
    val evaluations = viewModel.evaluations.collectAsState()
    val indicators = viewModel.indicators.collectAsState()
    val diagnostics = viewModel.diagnostics.collectAsState()

    val totalBottles = remember(evaluations.value) { 
        evaluations.value.sumOf { it.indicadores.entries.find { it.key.equals("Botellas", true) }?.value ?: 0 }
    }
    val totalTapas = remember(evaluations.value) {
        evaluations.value.sumOf { it.indicadores.entries.find { it.key.equals("Tapas", true) }?.value ?: 0 }
    }

    var selectedEvaluation by remember { mutableStateOf<Evaluation?>(null) }

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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {


            item {
                Text(
                    "Gestión de Indicadores",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EcoColors.TextDark
                )
            }

            items(indicators.value) { indicator ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(indicator.nombre, fontWeight = FontWeight.Bold)
                            if (indicator.nombre.equals("Botellas", true)) {
                                Text("Total: $totalBottles Kg", style = MaterialTheme.typography.bodySmall, color = EcoColors.AdminPrimary, fontWeight = FontWeight.Bold)
                            } else if (indicator.nombre.equals("Tapas", true)) {
                                Text("Total: $totalTapas Kg", style = MaterialTheme.typography.bodySmall, color = EcoColors.AdminPrimary, fontWeight = FontWeight.Bold)
                            }
                            Text(indicator.categoria, style = MaterialTheme.typography.bodySmall)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(
                                checked = indicator.activo,
                                onCheckedChange = { viewModel.toggleIndicator(indicator) },
                                colors = SwitchDefaults.colors(checkedThumbColor = EcoColors.AdminPrimary)
                            )
                            IconButton(onClick = { viewModel.deleteIndicator(indicator.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red.copy(alpha = 0.6f))
                            }
                        }
                    }
                }
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
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(nombre, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Spacer(modifier = Modifier.width(24.dp))
                            Text(String.format(Locale.getDefault(), "%.2f", avg), color = EcoColors.AdminPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (selectedEvaluation != null) {
        val evaluation = selectedEvaluation!!
        val roomName = rooms.value.find { it.id == evaluation.roomId }?.nombre ?: evaluation.roomId

        AlertDialog(
            onDismissRequest = { selectedEvaluation = null },
            title = { Text("Calificar Evaluación: $roomName") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (evaluation.evidenciasUrls.isNotEmpty()) {
                        Text("Evidencias:", fontWeight = FontWeight.Bold)
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.height(200.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(evaluation.evidenciasUrls) { url ->
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

                    Text("Observaciones del Docente:", fontWeight = FontWeight.Bold)
                    Text(evaluation.observaciones.ifEmpty { "Sin observaciones." }, style = MaterialTheme.typography.bodyMedium)

                    val diagnostic = diagnostics.value.find { it.roomId == evaluation.roomId }
                    if (diagnostic != null) {
                        Text("Diagnóstico Inicial (Línea Base):", fontWeight = FontWeight.Bold)
                        Card(
                            colors = CardDefaults.cardColors(containerColor = EcoColors.AdminPrimary.copy(alpha = 0.05f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                DiagnosticRow("Limpieza", diagnostic.estadoLimpieza)
                                DiagnosticRow("Residuos", diagnostic.clasificacionResiduos)
                                DiagnosticRow("Energía", diagnostic.ahorroEnergia)
                                DiagnosticRow("Mobiliario", diagnostic.cuidadoMobiliario)
                                DiagnosticRow("Participación", diagnostic.participacionAmbiental)
                            }
                        }
                    }

                    Text("Puntajes por Indicador:", fontWeight = FontWeight.Bold)
                    evaluation.indicadores.forEach { (name, value) ->
                        val unit = if (name.equals("Botellas", ignoreCase = true) || name.equals("Tapas", ignoreCase = true)) "Kg" else "pts"
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(name, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                            Text("$value $unit", fontWeight = FontWeight.Bold, color = EcoColors.AdminPrimary)
                        }
                    }

                    HorizontalDivider()
                    Text("Puntaje Total Automático:", fontWeight = FontWeight.Bold)
                    Text("${evaluation.puntajeObtenido} / 100", 
                        style = MaterialTheme.typography.headlineSmall, 
                        fontWeight = FontWeight.ExtraBold,
                        color = EcoColors.AdminPrimary
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { selectedEvaluation = null }) { Text("Cerrar") }
            }
        )
    }
}

@Composable
private fun DiagnosticRow(label: String, value: Int) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Text("$value/10", fontWeight = FontWeight.Bold)
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
