package com.example.myapplication.ui.teacher

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.domain.model.Evaluation
import com.example.myapplication.ui.common.QRScanner
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.theme.EcoColors
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.viewmodel.EvaluationViewModel

@Composable
fun TeacherDashboard(
    navController: NavController,
    viewModel: EvaluationViewModel = hiltViewModel()
) {
    var showScanner by remember { mutableStateOf(false) }
    val recentEvaluations by viewModel.recentEvaluations.collectAsState()

    if (showScanner) {
        QRScanner { result ->
            showScanner = false
            if (result != null) {
                navController.navigate(Screen.EvaluationForm.createRoute(result))
            }
        }
    }

    TeacherDashboardContent(
        recentEvaluations = recentEvaluations,
        onScanClick = { showScanner = true }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDashboardContent(
    recentEvaluations: List<Evaluation>,
    onScanClick: () -> Unit
) {
    Scaffold(
        containerColor = EcoColors.MintBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Panel Docente", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = EcoColors.DocentePrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EcoColors.DocentePrimary.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "¡Bienvenido, Docente!",
                        style = MaterialTheme.typography.headlineSmall,
                        color = EcoColors.DocentePrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Inicia una nueva evaluación ambiental hoy.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = EcoColors.TextDark
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = onScanClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EcoColors.DocentePrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Escanear QR del Salón")
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Evaluaciones Recientes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EcoColors.TextDark
                )
                TextButton(onClick = { /* Ver todo */ }) {
                    Text("Ver todas", color = EcoColors.DocentePrimary)
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(recentEvaluations) { evaluation ->
                    ListItem(
                        headlineContent = { Text("Salón ${evaluation.roomId}", fontWeight = FontWeight.Bold, color = EcoColors.TextDark) },
                        supportingContent = { 
                            val date = java.text.SimpleDateFormat("dd/MM HH:mm", java.util.Locale.getDefault())
                                .format(java.util.Date(evaluation.fecha))
                            Text("Puntaje: ${evaluation.puntajeObtenido} pts - $date", color = EcoColors.TextMuted) 
                        },
                        leadingContent = { Icon(Icons.Default.History, contentDescription = null, tint = EcoColors.DocentePrimary) },
                        trailingContent = { Text("Ver", color = EcoColors.DocentePrimary, fontWeight = FontWeight.Bold) },
                        colors = ListItemDefaults.colors(containerColor = Color.White),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TeacherDashboardPreview() {
    MyApplicationTheme {
        TeacherDashboardContent(
            recentEvaluations = emptyList(),
            onScanClick = {}
        )
    }
}
