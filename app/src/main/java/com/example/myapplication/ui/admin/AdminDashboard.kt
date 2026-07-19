package com.example.myapplication.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.domain.model.Course
import com.example.myapplication.ui.viewmodel.RankingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(
    navController: NavController,
    viewModel: RankingViewModel = hiltViewModel()
) {
    val ranking by viewModel.ranking.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Administración EcoLibertad", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { /* Refresh data */ }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                    }
                }
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
            item {
                Text(
                    "Métricas Globales",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Cursos",
                        value = "${ranking.size}",
                        icon = Icons.Default.Face,
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Evaluaciones",
                        value = "128", // Placeholder
                        icon = Icons.Default.Face,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                }
            }

            item {
                Text(
                    "Ranking de Cursos",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(ranking) { course ->
                AdminCourseItem(course)
            }
        }
    }
}

@Composable
fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    containerColor: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null)
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(title, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun AdminCourseItem(course: Course) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        ListItem(
            headlineContent = { Text(course.nombre, fontWeight = FontWeight.SemiBold) },
            supportingContent = { Text("Puntos acumulados: ${course.puntosTotales}") },
            trailingContent = {
                Text(
                    "#${course.posicionRanking}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        )
    }
}
