package com.example.myapplication.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventManagementScreen(
    navController: NavController,
    viewModel: ManagementViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val events by viewModel.events.collectAsState()
    val user by authViewModel.user.collectAsState()
    val role = user?.rol ?: UserRole.ADMIN
    
    val primaryColor = EcoColors.getPrimaryColor(role)
    val backgroundColor = if (role == UserRole.ADMIN) EcoColors.AdminBackground else EcoColors.MintBackground
    
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Eventos", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar Evento", tint = Color.White)
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
            items(events) { event ->
                val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(event.fecha))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = primaryColor)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(event.titulo, fontWeight = FontWeight.Bold, color = EcoColors.TextDark)
                            Text(dateStr, style = MaterialTheme.typography.bodySmall, color = primaryColor)
                            Text(event.descripcion, style = MaterialTheme.typography.bodySmall, color = EcoColors.TextMuted)
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            var titulo by remember { mutableStateOf("") }
            var descripcion by remember { mutableStateOf("") }
            var selectedDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
            var showDatePicker by remember { mutableStateOf(false) }
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            selectedDate = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                            showDatePicker = false
                        }) { Text("Aceptar") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Nuevo Evento") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = titulo,
                            onValueChange = { titulo = it },
                            label = { Text("Título") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = descripcion,
                            onValueChange = { descripcion = it },
                            label = { Text("Descripción") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedDate))
                        OutlinedTextField(
                            value = formattedDate,
                            onValueChange = { },
                            label = { Text("Fecha del Evento") },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = "Seleccionar fecha")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (titulo.isNotBlank()) {
                            viewModel.createEvent(titulo, descripcion, selectedDate)
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
