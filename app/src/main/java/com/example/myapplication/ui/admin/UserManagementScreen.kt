package com.example.myapplication.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.model.UserRole
import com.example.myapplication.ui.theme.EcoColors
import com.example.myapplication.ui.viewmodel.ManagementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    navController: NavController,
    viewModel: ManagementViewModel = hiltViewModel()
) {
    val users by viewModel.users.collectAsState()
    val courses by viewModel.courses.collectAsState()
    val rooms by viewModel.rooms.collectAsState()

    Scaffold(
        containerColor = EcoColors.MintBackground,
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Usuarios", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.White)
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
            val sortedUsers = users.sortedWith(compareBy({ it.rol != UserRole.INVITADO }, { it.nombre }))
            
            items(sortedUsers) { user ->
                UserItem(user, courses, rooms) { newRole, courseId ->
                    viewModel.updateUserRole(user.uid, newRole, courseId)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserItem(
    user: User, 
    courses: List<com.example.myapplication.domain.model.Course>,
    rooms: List<com.example.myapplication.domain.model.Room>,
    onRoleChange: (UserRole, String?) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                if (user.rol == UserRole.INVITADO) Icons.Outlined.Person else Icons.Default.Person,
                contentDescription = null, 
                tint = if (user.rol == UserRole.INVITADO) Color.Gray else EcoColors.AdminPrimary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(user.nombre, fontWeight = FontWeight.Bold, color = EcoColors.TextDark)
                Text(user.email, style = MaterialTheme.typography.bodySmall, color = EcoColors.TextMuted)
                Text(
                    "Rol: ${user.rol.name}", 
                    style = MaterialTheme.typography.labelSmall, 
                    color = if (user.rol == UserRole.INVITADO) Color.Gray else EcoColors.AdminPrimary
                )
                if (user.rol == UserRole.ESTUDIANTE && !user.courseId.isNullOrEmpty()) {
                    val courseName = courses.find { it.id == user.courseId }?.nombre 
                        ?: rooms.find { it.id == user.courseId }?.nombre 
                        ?: "No asignado"
                    Text("Asignación: $courseName", style = MaterialTheme.typography.labelSmall, color = EcoColors.PrimaryGreen)
                }
            }
            TextButton(onClick = { showDialog = true }) {
                Text(if (user.rol == UserRole.INVITADO) "Asignar Rol" else "Cambiar Rol")
            }
        }
    }

    if (showDialog) {
        var selectedRole by remember { mutableStateOf(user.rol) }
        var selectedCourseId by remember { mutableStateOf(user.courseId) }
        var expanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Gestionar Usuario: ${user.nombre}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Seleccionar Rol:", style = MaterialTheme.typography.titleSmall)
                    UserRole.values().forEach { role ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            RadioButton(
                                selected = selectedRole == role,
                                onClick = { selectedRole = role }
                            )
                            Text(role.name, modifier = Modifier.padding(start = 8.dp))
                        }
                    }

                    if (selectedRole == UserRole.ESTUDIANTE) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Asignar Salón o Aula (QR):", style = MaterialTheme.typography.titleSmall)
                        
                        val currentAssignmentName = courses.find { it.id == selectedCourseId }?.nombre 
                            ?: rooms.find { it.id == selectedCourseId }?.nombre 
                            ?: "Sin asignación"
                        
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = currentAssignmentName,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                shape = RoundedCornerShape(8.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Sin asignación") },
                                    onClick = {
                                        selectedCourseId = null
                                        expanded = false
                                    }
                                )
                                if (courses.isNotEmpty()) {
                                    Text("SALONES", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(8.dp))
                                    courses.forEach { course ->
                                        DropdownMenuItem(
                                            text = { Text(course.nombre) },
                                            onClick = {
                                                selectedCourseId = course.id
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                                if (rooms.isNotEmpty()) {
                                    Text("AULAS (QR)", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(8.dp))
                                    rooms.forEach { room ->
                                        DropdownMenuItem(
                                            text = { Text("${room.nombre} (${room.bloque})") },
                                            onClick = {
                                                selectedCourseId = room.id
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRoleChange(selectedRole, selectedCourseId)
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EcoColors.AdminPrimary)
                ) {
                    Text("Guardar Cambios")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
            }
        )
    }
}
