package com.example.myapplication.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import com.example.myapplication.domain.model.Evaluation
import com.example.myapplication.ui.theme.EcoColors
import com.example.myapplication.ui.viewmodel.EvidenceViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvidenceManagementScreen(
    onBack: () -> Unit,
    viewModel: EvidenceViewModel = hiltViewModel()
) {
    val evaluations by viewModel.evidenceEvaluations.collectAsState()
    val rooms by viewModel.rooms.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Evidencias", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Filtrar por curso o fecha */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filtrar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = EcoColors.TextDark
                )
            )
        },
        containerColor = EcoColors.MintBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                "Galería de evidencias reales",
                style = MaterialTheme.typography.titleMedium,
                color = EcoColors.TextDark,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (evaluations.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay evidencias registradas aún", color = EcoColors.TextMuted)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Una evaluación puede tener múltiples fotos
                    evaluations.forEach { evaluation ->
                        items(evaluation.evidenciasUrls) { url ->
                            val roomName = rooms.find { it.id == evaluation.roomId }?.nombre ?: evaluation.roomId
                            EvidenceCard(url, evaluation, roomName)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EvidenceCard(imageUrl: String, evaluation: Evaluation, roomName: String) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val dateString = dateFormat.format(Date(evaluation.fecha))
    var showDialog by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true }
    ) {
        Column {
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = "Evidencia de $roomName",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    }
                },
                error = {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null, tint = Color.Gray)
                    }
                }
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = roomName,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    color = EcoColors.AdminPrimary
                )
                Text(
                    dateString,
                    style = MaterialTheme.typography.labelSmall,
                    color = EcoColors.TextMuted
                )
                Text(
                    "Puntos: ${evaluation.puntajeObtenido}",
                    style = MaterialTheme.typography.labelSmall,
                    color = EcoColors.PrimaryGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Detalle de Evidencia") },
            text = {
                Column {
                    SubcomposeAsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Salón: $roomName", fontWeight = FontWeight.Bold)
                    Text("Fecha: $dateString")
                    Text("Puntos: ${evaluation.puntajeObtenido}")
                    if (evaluation.observaciones.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Observaciones:", fontWeight = FontWeight.Bold)
                        Text(evaluation.observaciones)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}
