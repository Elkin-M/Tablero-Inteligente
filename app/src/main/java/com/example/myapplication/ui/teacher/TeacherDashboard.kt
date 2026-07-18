package com.example.myapplication.ui.teacher

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.ui.common.QRScanner
import com.example.myapplication.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDashboard(navController: NavController) {
    var showScanner by remember { mutableStateOf(false) }

    if (showScanner) {
        QRScanner { result ->
            showScanner = false
            if (result != null) {
                navController.navigate(Screen.EvaluationForm.createRoute(result))
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Panel Docente", fontWeight = FontWeight.Bold) }
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("¡Bienvenido, Docente!", style = MaterialTheme.typography.headlineSmall)
                    Text("Inicia una nueva evaluación ambiental hoy.", style = MaterialTheme.typography.bodyMedium)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = { showScanner = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Escanear QR del Salón")
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Evaluaciones Recientes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                TextButton(onClick = { /* Ver todo */ }) {
                    Text("Ver todas")
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(3) { index ->
                    ListItem(
                        headlineContent = { Text("Salón ${201 + index}") },
                        supportingContent = { Text("Puntaje: ${85 + index} pts - Hace ${index + 1} h") },
                        leadingContent = { Icon(Icons.Default.History, contentDescription = null) },
                        trailingContent = { Text("Ver", color = MaterialTheme.colorScheme.primary) }
                    )
                }
            }
        }
    }
}
