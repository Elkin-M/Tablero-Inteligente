package com.example.myapplication.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.domain.model.Course
import com.example.myapplication.domain.model.EcoTip
import com.example.myapplication.domain.model.Evaluation
import com.example.myapplication.ui.common.EvaluationDetailDialog
import com.example.myapplication.ui.common.ProfileScreen
import com.example.myapplication.ui.theme.EcoColors
import com.example.myapplication.ui.viewmodel.AuthViewModel
import com.example.myapplication.ui.viewmodel.RankingViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StudentDashboard(
    navController: NavController,
    viewModel: RankingViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val ranking by viewModel.ranking.collectAsState(initial = emptyList())
    val user by viewModel.currentUser.collectAsState()
    val evaluations by viewModel.userEvaluations.collectAsState()
    val rooms by viewModel.rooms.collectAsState()
    val tips by viewModel.tips.collectAsState(initial = emptyList())

    StudentDashboardContent(
        ranking = ranking,
        user = user,
        evaluations = evaluations,
        rooms = rooms,
        tips = tips,
        onLogout = { 
            authViewModel.logout {
                navController.navigate("login") {
                    popUpTo(0)
                }
            }
        },
        authViewModel = authViewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboardContent(
    ranking: List<Course>,
    user: com.example.myapplication.domain.model.User? = null,
    evaluations: List<Evaluation> = emptyList(),
    rooms: List<com.example.myapplication.domain.model.Room> = emptyList(),
    tips: List<EcoTip> = emptyList(),
    onLogout: () -> Unit = {},
    authViewModel: AuthViewModel
) {
    val userCourse = ranking.find { it.id == user?.courseId }
    val userRoom = rooms.find { it.id == user?.courseId }
    val displayName = userCourse?.nombre ?: userRoom?.nombre ?: "Sin Salón Asignado"
    val userRank = if (userCourse != null) ranking.indexOf(userCourse) + 1 else null

    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = EcoColors.MintBackground,
        topBar = {
            Surface(
                color = EcoColors.EstudiantePrimary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Perfil",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Hola, ${user?.nombre ?: "Estudiante"}",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "EcoRanking IA",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
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
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = EcoColors.EstudiantePrimary
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("Inicio") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EcoColors.EstudiantePrimary,
                        selectedTextColor = EcoColors.EstudiantePrimary,
                        indicatorColor = EcoColors.EstudiantePrimary.copy(alpha = 0.1f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("Ranking") },
                    icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Ranking") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EcoColors.EstudiantePrimary,
                        selectedTextColor = EcoColors.EstudiantePrimary,
                        indicatorColor = EcoColors.EstudiantePrimary.copy(alpha = 0.1f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    label = { Text("Perfil") },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EcoColors.EstudiantePrimary,
                        selectedTextColor = EcoColors.EstudiantePrimary,
                        indicatorColor = EcoColors.EstudiantePrimary.copy(alpha = 0.1f)
                    )
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedTab) {
                0 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item { Spacer(modifier = Modifier.height(8.dp)) }

                        item {
                            Text("Tu Salón: $displayName", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(containerColor = EcoColors.EstudiantePrimary.copy(alpha = 0.1f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(20.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(CircleShape)
                                            .background(EcoColors.EstudiantePrimary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(
                                            text = displayName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = EcoColors.EstudiantePrimary
                                        )
                                        Text(
                                            text = if (userRank != null) "Puesto Actual: #$userRank" else "---",
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = EcoColors.EstudiantePrimary
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Text("EcoTips del Día", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }

                        if (tips.isEmpty()) {
                            item {
                                Text("No hay tips hoy. ¡Sé un guardián ambiental!", color = EcoColors.TextMuted)
                            }
                        } else {
                            items(tips.filter { it.activa }) { tip ->
                                EcoTipCard(tip)
                            }
                        }

                        item {
                            Text("Mis Evaluaciones", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }

                        if (evaluations.isEmpty()) {
                            item {
                                Text(
                                    "Aún no hay evaluaciones para tu salón.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = EcoColors.TextMuted,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        } else {
                            items(evaluations.sortedByDescending { it.fecha }.take(3)) { eval ->
                                var showModal by remember { mutableStateOf(false) }
                                Box(modifier = Modifier.clickable { showModal = true }) {
                                    EvaluationItem(eval)
                                }
                                if (showModal) {
                                    EvaluationDetailDialog(evaluation = eval, roomName = displayName, onDismiss = { showModal = false })
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
                1 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                        item {
                            Text("Líderes Ambientales", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }

                        itemsIndexed(ranking) { index, curso ->
                            val color = when(index) {
                                0 -> Color(0xFFFFD700) // Oro
                                1 -> Color(0xFFC0C0C0) // Plata
                                2 -> Color(0xFFCD7F32) // Bronce
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }

                            ListItem(
                                headlineContent = { 
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Salón ${curso.nombre}", fontWeight = FontWeight.Bold, color = EcoColors.TextDark)
                                        Spacer(modifier = Modifier.weight(1f))
                                        Text(
                                            text = "${curso.puntosTotales} pts",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = EcoColors.EstudiantePrimary
                                        )
                                    }
                                },
                                supportingContent = { Text("Puntos acumulados esta temporada", color = EcoColors.TextMuted) },
                                leadingContent = {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(color),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("${index + 1}", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
                                    }
                                },
                                trailingContent = {
                                    if(index == 0) Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = color)
                                },
                                colors = ListItemDefaults.colors(containerColor = Color.White),
                                modifier = Modifier.clip(RoundedCornerShape(16.dp))
                            )
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
                2 -> ProfileScreen(authViewModel = authViewModel, onLogout = onLogout)
            }
        }
    }
}

@Composable
fun EcoTipCard(tip: EcoTip) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = EcoColors.EstudiantePrimary.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Lightbulb,
                contentDescription = null,
                tint = EcoColors.EstudiantePrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = tip.contenido,
                style = MaterialTheme.typography.bodyMedium,
                color = EcoColors.TextDark
            )
        }
    }
}

@Composable
fun EvaluationItem(evaluation: Evaluation) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val dateString = dateFormat.format(Date(evaluation.fecha))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(EcoColors.EstudiantePrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Assignment,
                            contentDescription = null,
                            tint = EcoColors.EstudiantePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Evaluación Semanal",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = EcoColors.TextDark
                        )
                        Text(
                            text = dateString,
                            style = MaterialTheme.typography.labelSmall,
                            color = EcoColors.TextMuted
                        )
                    }
                }

                Surface(
                    color = EcoColors.EstudiantePrimary,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${evaluation.puntajeObtenido} pts",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            if (evaluation.indicadores.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = EcoColors.Divider, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    evaluation.indicadores.entries.take(3).forEach { (key, value) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = key.replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.labelSmall,
                                color = EcoColors.TextMuted
                            )
                            val unit = if (key.equals("Botellas", ignoreCase = true) || key.equals("Tapas", ignoreCase = true)) " Kg" else "/5"
                            Text(
                                text = "$value$unit",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = EcoColors.EstudiantePrimary
                            )
                        }
                    }
                }
            }

            if (evaluation.observaciones.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = evaluation.observaciones,
                    style = MaterialTheme.typography.bodySmall,
                    color = EcoColors.TextMuted,
                    maxLines = 2,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}
