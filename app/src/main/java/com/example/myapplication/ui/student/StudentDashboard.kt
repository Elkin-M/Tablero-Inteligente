package com.example.myapplication.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.myapplication.domain.model.Evaluation
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

    StudentDashboardContent(
        ranking = ranking,
        user = user,
        evaluations = evaluations,
        onLogout = { 
            authViewModel.logout {
                navController.navigate("login") {
                    popUpTo(0)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboardContent(
    ranking: List<Course>,
    user: com.example.myapplication.domain.model.User? = null,
    evaluations: List<Evaluation> = emptyList(),
    onLogout: () -> Unit = {}
) {
    val userCourse = ranking.find { it.id == user?.courseId }
    val userRank = if (userCourse != null) ranking.indexOf(userCourse) + 1 else null

    Scaffold(
        containerColor = EcoColors.MintBackground,
        topBar = {
            TopAppBar(
                title = { Text("EcoRanking IA", fontWeight = FontWeight.Bold, color = Color.White) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar Sesión", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EcoColors.EstudiantePrimary,
                    titleContentColor = Color.White
                ),
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

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
                                text = if (userCourse != null) "Tu Salón: ${userCourse.nombre}" else "Sin Salón Asignado",
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
                    EvaluationItem(eval)
                }
            }

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
                    headlineContent = { Text("Salón ${curso.nombre}", fontWeight = FontWeight.Bold, color = EcoColors.TextDark) },
                    supportingContent = { Text("${curso.puntosTotales} puntos acumulados", color = EcoColors.TextMuted) },
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
}

@Composable
fun EvaluationItem(evaluation: Evaluation) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val dateString = dateFormat.format(Date(evaluation.fecha))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Assignment,
                contentDescription = null,
                tint = EcoColors.PrimaryGreen,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(dateString, style = MaterialTheme.typography.labelSmall, color = EcoColors.TextMuted)
                Text("Puntaje: ${evaluation.puntajeObtenido}", fontWeight = FontWeight.Bold)
            }
            if (evaluation.observaciones.isNotEmpty()) {
                Text(
                    evaluation.observaciones,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    modifier = Modifier.widthIn(max = 100.dp),
                    color = EcoColors.TextMuted
                )
            }
        }
    }
}
