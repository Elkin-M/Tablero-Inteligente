package com.example.myapplication.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.domain.model.Course
import com.example.myapplication.ui.theme.EcoColors
import com.example.myapplication.ui.viewmodel.RankingViewModel

@Composable
fun StudentDashboard(
    navController: NavController,
    viewModel: RankingViewModel = hiltViewModel()
) {
    val ranking by viewModel.ranking.collectAsState(initial = emptyList<Course>())

    StudentDashboardContent(ranking = ranking)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboardContent(
    ranking: List<Course>
) {
    Scaffold(
        containerColor = EcoColors.MintBackground,
        topBar = {
            LargeTopAppBar(
                title = { Text("EcoRanking IA", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = EcoColors.EstudiantePrimary,
                    scrolledContainerColor = EcoColors.EstudiantePrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = EcoColors.EstudiantePrimary.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(EcoColors.EstudiantePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Tu Curso: 11-02", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = EcoColors.EstudiantePrimary)
                            Text("Puesto Actual: #3", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = EcoColors.EstudiantePrimary)
                        }
                    }
                }
            }

            item {
                Text("Líderes Ambientales", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            itemsIndexed(ranking) { index, curso ->
                val color = when(index) {
                    0 -> Color(0xFFFFD700) // Oro
                    1 -> Color(0xFFC0C0C0) // Plata
                    2 -> Color(0xFFCD7F32) // Bronce
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }

                ListItem(
                    headlineContent = { Text("Curso ${curso.nombre}", fontWeight = FontWeight.Bold, color = EcoColors.TextDark) },
                    supportingContent = { Text("${curso.puntosTotales} puntos acumulados", color = EcoColors.TextMuted) },
                    leadingContent = {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(color),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${index + 1}", fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    },
                    trailingContent = {
                        if(index == 0) Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = color)
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.White),
                    modifier = Modifier.clip(RoundedCornerShape(16.dp))
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StudentDashboardPreview() {
    com.example.myapplication.ui.theme.MyApplicationTheme {
        StudentDashboardContent(
            ranking = listOf(
                Course(id = "1", nombre = "11-01", puntosTotales = 500),
                Course(id = "2", nombre = "11-02", puntosTotales = 450),
                Course(id = "3", nombre = "10-01", puntosTotales = 400),
                Course(id = "4", nombre = "9-03", puntosTotales = 350)
            )
        )
    }
}
