package com.example.myapplication.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.domain.model.Challenge
import com.example.myapplication.ui.theme.EcoColors
import com.example.myapplication.ui.viewmodel.ManagementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeManagementScreen(
    navController: NavController,
    viewModel: ManagementViewModel = hiltViewModel()
) {
    val challenges by viewModel.challenges.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = EcoColors.MintBackground,
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Retos", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar Reto", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EcoColors.AdminPrimary)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(challenges) { challenge ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = EcoColors.AdminPrimary)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(challenge.titulo, fontWeight = FontWeight.Bold, color = EcoColors.TextDark)
                                Text(challenge.descripcion, style = MaterialTheme.typography.bodySmall, color = EcoColors.TextMuted)
                                Text("${challenge.puntos} puntos", style = MaterialTheme.typography.labelSmall, color = EcoColors.AdminPrimary)
                            }
                        }
                        
                        Switch(
                            checked = challenge.activo,
                            onCheckedChange = { viewModel.toggleChallengeStatus(challenge) }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            var titulo by remember { mutableStateOf("") }
            var descripcion by remember { mutableStateOf("") }
            var puntos by remember { mutableStateOf("50") }

            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Nuevo Reto Ecológico") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título") })
                        OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") })
                        OutlinedTextField(value = puntos, onValueChange = { puntos = it }, label = { Text("Puntos") })
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (titulo.isNotBlank()) {
                            viewModel.createChallenge(titulo, descripcion, puntos.toIntOrNull() ?: 0)
                            showAddDialog = false
                        }
                    }) { Text("Crear") }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) { Text("Cancelar") }
                }
            )
        }
    }
}
