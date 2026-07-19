package com.example.myapplication.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.myapplication.domain.model.BaselineDiagnostic
import com.example.myapplication.domain.model.Course
import com.example.myapplication.domain.model.Room
import com.example.myapplication.ui.viewmodel.RankingViewModel
import com.example.myapplication.ui.viewmodel.RoomFormState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(
    navController: NavController,
    viewModel: RankingViewModel = hiltViewModel()
) {
    val ranking by viewModel.ranking.collectAsState()
    val rooms by viewModel.rooms.collectAsState()
    val formState by viewModel.formState.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = when (selectedTab) {
                            0 -> "Inicio EcoLibertad"
                            1 -> "Ranking de Salones"
                            2 -> "Inscripción de Salones"
                            else -> "Modificación de Salones"
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("Inicio") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("Ranking") },
                    icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Ranking") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    label = { Text("Salones") },
                    icon = { Icon(Icons.Default.MeetingRoom, contentDescription = "Inscripción de Salones") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    label = { Text("Editar") },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Modificación de Salones") }
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
                0 -> TabInicio(ranking = ranking, totalSalones = rooms.size)
                1 -> TabRankingCompleto(ranking = ranking)
                2 -> TabInscripcionSalones(
                    formState = formState,
                    onRegistrarSalon = viewModel::registrarSalon,
                    onRegistrarLineaBase = viewModel::registrarLineaBase,
                    onResetForm = viewModel::resetFormState,
                    rooms = rooms
                )
                3 -> TabModificacionSalones(
                    rooms = rooms,
                    onDesactivar = viewModel::desactivarSalon
                )
            }
        }
    }
}

// ==========================================
// APARTADO 1: INICIO
// ==========================================
@Composable
fun TabInicio(ranking: List<Course>, totalSalones: Int) {
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
                    title = "Salones Activos",
                    value = "$totalSalones",
                    icon = Icons.Default.MeetingRoom,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Puntos Totales",
                    value = "${ranking.sumOf { it.puntosTotales }}",
                    icon = Icons.Default.Recycling,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            }
        }

        if (ranking.isNotEmpty()) {
            item {
                Text(
                    "Salón Líder",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(ranking.first().nombre, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "${ranking.first().puntosTotales} puntos acumulados",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        } else {
            item {
                EmptyState(
                    icon = Icons.Default.MeetingRoom,
                    message = "Aún no hay salones registrados. Ve a la pestaña 'Salones' para inscribir el primero."
                )
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
                "Ranking de Salones",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        if (ranking.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Default.EmojiEvents,
                    message = "Todavía no hay puntajes. El ranking se llena automáticamente con las evaluaciones semanales."
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
fun TabInscripcionSalones(
    formState: RoomFormState,
    onRegistrarSalon: (String, String) -> Unit,
    onRegistrarLineaBase: (BaselineDiagnostic) -> Unit,
    onResetForm: () -> Unit,
    rooms: List<Room>
) {
    var nombreSalon by remember { mutableStateOf("") }
    var bloqueSalon by remember { mutableStateOf("") }

    // Línea base (opcional al momento de inscribir)
    var incluirLineaBase by remember { mutableStateOf(false) }
    var limpieza by remember { mutableStateOf(5f) }
    var clasificacionResiduos by remember { mutableStateOf(5f) }
    var ahorroEnergia by remember { mutableStateOf(5f) }
    var cuidadoMobiliario by remember { mutableStateOf(5f) }
    var participacionAmbiental by remember { mutableStateOf(5f) }

    LaunchedEffect(formState) {
        if (formState is RoomFormState.Success) {
            nombreSalon = ""
            bloqueSalon = ""
            incluirLineaBase = false
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Inscripción de Salones",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = nombreSalon,
                onValueChange = { nombreSalon = it },
                label = { Text("Nombre del Salón (ej. 8A)") },
                leadingIcon = { Icon(Icons.Default.MeetingRoom, contentDescription = null) },
                singleLine = true,
                enabled = formState !is RoomFormState.Loading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = bloqueSalon,
                onValueChange = { bloqueSalon = it },
                label = { Text("Bloque / Ubicación") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                singleLine = true,
                enabled = formState !is RoomFormState.Loading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar línea base ahora", style = MaterialTheme.typography.bodyLarge)
                Switch(checked = incluirLineaBase, onCheckedChange = { incluirLineaBase = it })
            }
        }

        if (incluirLineaBase) {
            item {
                Text(
                    "Diagnóstico Inicial",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    "Este diagnóstico se hace una sola vez, antes de iniciar las evaluaciones semanales.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            item { SliderIndicator("Estado de limpieza", Icons.Default.CleaningServices, limpieza) { limpieza = it } }
            item { SliderIndicator("Clasificación de residuos", Icons.Default.Recycling, clasificacionResiduos) { clasificacionResiduos = it } }
            item { SliderIndicator("Ahorro de energía", Icons.Default.Bolt, ahorroEnergia) { ahorroEnergia = it } }
            item { SliderIndicator("Cuidado del mobiliario", Icons.Default.Chair, cuidadoMobiliario) { cuidadoMobiliario = it } }
            item { SliderIndicator("Participación ambiental", Icons.Default.VolunteerActivism, participacionAmbiental) { participacionAmbiental = it } }
        }

        if (formState is RoomFormState.Error) {
            item {
                Text(
                    formState.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        if (formState is RoomFormState.Success) {
            item {
                Text(
                    "Salón registrado correctamente",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    onRegistrarSalon(nombreSalon, bloqueSalon)
                    // Nota: la línea base queda pendiente de asociar al roomId real
                    // una vez el salón se crea (ver siguiente iteración: usar el id
                    // devuelto por el repositorio para llamar onRegistrarLineaBase).
                },
                enabled = formState !is RoomFormState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (formState is RoomFormState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Registrar Salón")
                }
            }
        }
    }
}

// ==========================================
// APARTADO 4: MODIFICACIÓN DE SALONES
// ==========================================
@Composable
fun TabModificacionSalones(
    rooms: List<Room>,
    onDesactivar: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "Modificación de Salones",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Text(
                "Desactiva salones que ya no participan en la competencia. No se eliminan datos históricos.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (rooms.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Default.Settings,
                    message = "No hay salones activos para modificar."
                )
            }
        } else {
            items(rooms) { room ->
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.MeetingRoom, contentDescription = null)
                            Column {
                                Text(room.nombre, fontWeight = FontWeight.SemiBold)
                                Text(room.bloque, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        IconButton(onClick = { onDesactivar(room.id) }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Desactivar salón",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
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
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
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
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        ListItem(
            leadingContent = {
                Icon(
                    Icons.Default.MeetingRoom,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
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

@Composable
fun SliderIndicator(
    label: String,
    icon: ImageVector,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            Text("${value.toInt()}/10", fontWeight = FontWeight.Bold)
        }
        Slider(value = value, onValueChange = onValueChange, valueRange = 0f..10f, steps = 9)
    }
}

@Composable
fun EmptyState(icon: ImageVector, message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}