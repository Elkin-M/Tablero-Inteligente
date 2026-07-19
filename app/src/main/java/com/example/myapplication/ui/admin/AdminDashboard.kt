package com.example.myapplication.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    // Estado local para controlar cuál de las 4 pestañas está activa
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = when (selectedTab) {
                            0 -> "Inicio EcoLibertad"
                            1 -> "Ranking General"
                            2 -> "Inscripción de Salones"
                            else -> "Modificación de Datos"
                        },
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { /* Refresh data */ }) {
                        Icon(Icons.Default.Face, contentDescription = "Actualizar")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("Inicio") },
                    icon = { Icon(Icons.Default.Face, contentDescription = "Inicio") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("Ranking") },
                    icon = { Icon(Icons.Default.Face, contentDescription = "Ranking") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    label = { Text("Salones") },
                    icon = { Icon(Icons.Default.Face, contentDescription = "Salones") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    label = { Text("Datos") },
                    icon = { Icon(Icons.Default.Face, contentDescription = "Datos") }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Renderiza el apartado correspondiente según la pestaña seleccionada
            when (selectedTab) {
                0 -> TabInicio(ranking = ranking)
                1 -> TabRankingCompleto(ranking = ranking)
                2 -> TabInscripcionSalones()
                3 -> TabModificacionDatos()
            }
        }
    }
}

// ==========================================
// APARTADO 1: INICIO (Resumen + Novedades)
// ==========================================
@Composable
fun TabInicio(ranking: List<Course>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
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
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Evaluaciones",
                    value = "128",
                    icon = Icons.Default.Face,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            }
        }

        item {
            Text(
                "Novedades",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Campaña de Reciclaje Activa",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Los salones del bloque principal han aumentado su recolección un 20% esta semana.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

// ==========================================
// APARTADO 2: RANKING
// ==========================================
@Composable
fun TabRankingCompleto(ranking: List<Course>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Ranking de Cursos",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        if (ranking.isEmpty()) {
            item {
                Text(
                    "Aún no hay cursos registrados en el ranking.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            }
        } else {
            items(ranking) { course ->
                AdminCourseItem(course)
            }
        }
    }
}

// ==========================================
// APARTADO 3: INSCRIPCIÓN DE SALONES
// ==========================================
@Composable
fun TabInscripcionSalones() {
    var nombreSalon by remember { mutableStateOf("") }
    var bloqueSalon by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Inscripción de Salones",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.Start).padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = nombreSalon,
            onValueChange = { nombreSalon = it },
            label = { Text("Nombre o Número del Salón") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = bloqueSalon,
            onValueChange = { bloqueSalon = it },
            label = { Text("Bloque / Ubicación") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                // Lógica para guardar salón (conectar con ViewModel/Repositorio)
                nombreSalon = ""
                bloqueSalon = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar Salón")
        }
    }
}

// ==========================================
// APARTADO 4: MODIFICACIÓN DE DATOS
// ==========================================
@Composable
fun TabModificacionDatos() {
    var metaReciclaje by remember { mutableStateOf("") }
    var modoMantenimiento by remember { mutableStateOf(false) }
    var notificacionesActivas by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Modificación de Datos",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Text(
            "Desde este apartado podrás actualizar los parámetros fundamentales del sistema, cambiar configuraciones base y editar la información de las métricas.",
            style = MaterialTheme.typography.bodyMedium
        )

        OutlinedTextField(
            value = metaReciclaje,
            onValueChange = { metaReciclaje = it },
            label = { Text("Meta de reciclaje mensual (kg)") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Modo mantenimiento", style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = modoMantenimiento,
                onCheckedChange = { modoMantenimiento = it }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Notificaciones automáticas", style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = notificacionesActivas,
                onCheckedChange = { notificacionesActivas = it }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { /* Lógica para persistir configuración (ViewModel/Repositorio) */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Cambios")
        }
    }
}

// ==========================================
// COMPONENTES REUTILIZABLES
// ==========================================
@Composable
fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color
) {
    Card(
        modifier = modifier,
        // Colores sólidos forzados: evita que Material You "lave" el fondo en pantallas físicas.
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
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