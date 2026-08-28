package com.example.myapplication.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.domain.model.Indicator
import com.example.myapplication.ui.theme.EcoColors
import com.example.myapplication.ui.viewmodel.ManagementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndicatorManagementScreen(
    navController: NavController,
    viewModel: ManagementViewModel = hiltViewModel()
) {
    val indicators by viewModel.indicators.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = EcoColors.MintBackground,
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Indicadores", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar", tint = Color.White)
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
            items(indicators) { indicator ->
                IndicatorCard(
                    indicator = indicator,
                    onToggleStatus = { viewModel.toggleIndicatorStatus(indicator) }
                )
            }
        }

        if (showAddDialog) {
            AddIndicatorDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { nombre, desc, cat, max ->
                    viewModel.createIndicator(nombre, desc, cat, max)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun IndicatorCard(indicator: Indicator, onToggleStatus: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.BarChart, contentDescription = null, tint = if (indicator.activo) EcoColors.AdminPrimary else Color.Gray)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(indicator.nombre, fontWeight = FontWeight.Bold, color = EcoColors.TextDark)
                Text(indicator.categoria, style = MaterialTheme.typography.bodySmall, color = EcoColors.TextMuted)
            }
            Switch(
                checked = indicator.activo,
                onCheckedChange = { onToggleStatus() },
                colors = SwitchDefaults.colors(checkedThumbColor = EcoColors.AdminPrimary)
            )
        }
    }
}

@Composable
fun AddIndicatorDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, Int) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Limpieza") }
    var valorMaximo by remember { mutableStateOf("5") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Indicador") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
                OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") })
                OutlinedTextField(value = categoria, onValueChange = { categoria = it }, label = { Text("Categoría") })
                OutlinedTextField(value = valorMaximo, onValueChange = { valorMaximo = it }, label = { Text("Valor Máximo") })
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(nombre, descripcion, categoria, valorMaximo.toIntOrNull() ?: 5) }) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
