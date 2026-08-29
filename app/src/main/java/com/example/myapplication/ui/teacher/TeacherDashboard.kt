package com.example.myapplication.ui.teacher

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.*
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
import com.example.myapplication.ui.common.EvaluationDetailDialog
import com.example.myapplication.ui.common.QRScanner
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.theme.EcoColors
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.viewmodel.EvaluationViewModel
import com.example.myapplication.ui.viewmodel.AuthViewModel

@Composable
fun TeacherDashboard(
    navController: NavController,
    viewModel: EvaluationViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
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
        onScanClick = { showScanner = true },
        onManageTips = { navController.navigate(Screen.TipManagement.route) },
        onManageEvents = { navController.navigate(Screen.EventManagement.route) },
        onLogout = {
            authViewModel.logout {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.TeacherDashboard.route) { inclusive = true }
                }
            }
        },
        viewModel = viewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDashboardContent(
    recentEvaluations: List<Evaluation>,
    onScanClick: () -> Unit,
    onManageTips: () -> Unit,
    onManageEvents: () -> Unit,
    onLogout: () -> Unit,
    viewModel: EvaluationViewModel
) {
    var showStudentList by remember { mutableStateOf(false) }
    val students by viewModel.students.collectAsState()
    val courses by viewModel.courses.collectAsState()
    val rooms by viewModel.rooms.collectAsState()

    Scaffold(
        containerColor = EcoColors.MintBackground,
        topBar = {
            Surface(
                color = EcoColors.DocentePrimary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Comité Ambiental",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { showStudentList = true }) {
                        Icon(Icons.Default.GroupAdd, contentDescription = "Asignar Estudiantes", tint = Color.White)
                    }
                    IconButton(onClick = onLogout) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Cerrar sesión",
                            tint = Color.White
                        )
                    }
                }
            }
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
                        "¡Bienvenido, Comité!",
                        style = MaterialTheme.typography.headlineSmall,
                        color = EcoColors.DocentePrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Gestiona el impacto ambiental de tu institución.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = EcoColors.TextDark
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onScanClick,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = EcoColors.DocentePrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Evaluar QR", style = MaterialTheme.typography.bodyMedium)
                        }
                        
                        OutlinedButton(
                            onClick = onManageTips,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Gestionar Tips", style = MaterialTheme.typography.bodyMedium)
                        }

                        OutlinedButton(
                            onClick = onManageEvents,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Event, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Gestionar Eventos", style = MaterialTheme.typography.bodyMedium)
                        }
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
                    val roomName = rooms.find { it.id == evaluation.roomId }?.nombre ?: evaluation.roomId
                    var showDetail by remember { mutableStateOf(false) }
                    
                    ListItem(
                        headlineContent = { Text("Salón $roomName", fontWeight = FontWeight.Bold, color = EcoColors.TextDark) },
                        supportingContent = { 
                            val date = java.text.SimpleDateFormat("dd/MM HH:mm", java.util.Locale.getDefault())
                                .format(java.util.Date(evaluation.fecha))
                            Text("Puntaje: ${evaluation.puntajeObtenido} pts - $date", color = EcoColors.TextMuted) 
                        },
                        leadingContent = { Icon(Icons.Default.History, contentDescription = null, tint = EcoColors.DocentePrimary) },
                        trailingContent = { 
                            TextButton(onClick = { showDetail = true }) {
                                Text("Ver", color = EcoColors.DocentePrimary, fontWeight = FontWeight.Bold)
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.White),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    if (showDetail) {
                        EvaluationDetailDialog(evaluation = evaluation, roomName = roomName, onDismiss = { showDetail = false })
                    }
                }
            }
        }
    }

    if (showStudentList) {
        StudentAssignmentDialog(
            students = students.filter { it.rol == com.example.myapplication.domain.model.UserRole.ESTUDIANTE },
            courses = courses,
            rooms = rooms,
            onDismiss = { showStudentList = false },
            onAssign = { studentUid, courseId ->
                viewModel.updateStudentCourse(studentUid, courseId)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentAssignmentDialog(
    students: List<com.example.myapplication.domain.model.User>,
    courses: List<com.example.myapplication.domain.model.Course>,
    rooms: List<com.example.myapplication.domain.model.Room>,
    onDismiss: () -> Unit,
    onAssign: (String, String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Asignar Estudiantes") },
        text = {
            Box(modifier = Modifier.heightIn(max = 400.dp)) {
                if (students.isEmpty()) {
                    Text("No hay estudiantes registrados.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(students) { student ->
                            var expanded by remember { mutableStateOf(false) }
                            val currentName = courses.find { it.id == student.courseId }?.nombre 
                                ?: rooms.find { it.id == student.courseId }?.nombre 
                                ?: "Sin asignar"

                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, EcoColors.Divider)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(student.nombre, fontWeight = FontWeight.Bold)
                                    Text(student.email, style = MaterialTheme.typography.bodySmall)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    
                                    ExposedDropdownMenuBox(
                                        expanded = expanded,
                                        onExpandedChange = { expanded = !expanded }
                                    ) {
                                        OutlinedTextField(
                                            value = currentName,
                                            onValueChange = {},
                                            readOnly = true,
                                            label = { Text("Salón / Aula QR") },
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                                            textStyle = MaterialTheme.typography.bodySmall,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        ExposedDropdownMenu(
                                            expanded = expanded,
                                            onDismissRequest = { expanded = false }
                                        ) {
                                            if (courses.isNotEmpty()) {
                                                Text("SALONES", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(8.dp))
                                                courses.forEach { course ->
                                                    DropdownMenuItem(
                                                        text = { Text(course.nombre) },
                                                        onClick = {
                                                            onAssign(student.uid, course.id)
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
                                                            onAssign(student.uid, room.id)
                                                            expanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun TeacherDashboardPreview() {
    MyApplicationTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Text("Teacher Dashboard Preview (Requires ViewModel)")
        }
    }
}
