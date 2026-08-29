package com.example.myapplication.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.domain.model.UserRole
import com.example.myapplication.ui.theme.EcoColors
import com.example.myapplication.ui.viewmodel.AuthViewModel
import com.example.myapplication.ui.viewmodel.ManagementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipManagementScreen(
    navController: NavController,
    viewModel: ManagementViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val tips by viewModel.tips.collectAsState()
    val user by authViewModel.user.collectAsState()
    val role = user?.rol ?: UserRole.ADMIN
    
    val primaryColor = EcoColors.getPrimaryColor(role)
    val backgroundColor = if (role == UserRole.ADMIN) EcoColors.AdminBackground else EcoColors.MintBackground
    
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Tips", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar Tip", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryColor)
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
            items(tips) { tip ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = primaryColor)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(tip.contenido, fontWeight = FontWeight.Bold, color = EcoColors.TextDark)
                            Text("Por: ${tip.autor}", style = MaterialTheme.typography.bodySmall, color = EcoColors.TextMuted)
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            var contenido by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Nuevo Tip Ambiental") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = contenido,
                            onValueChange = { contenido = it },
                            label = { Text("Contenido") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (contenido.isNotBlank()) {
                            viewModel.createTip(contenido)
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
