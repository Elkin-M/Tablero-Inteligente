package com.example.myapplication.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.domain.model.BaselineDiagnostic
import com.example.myapplication.domain.model.Course
import com.example.myapplication.domain.model.Room
import com.example.myapplication.ui.common.ComingSoonScreen
import com.example.myapplication.ui.theme.EcoColors
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.viewmodel.RankingViewModel
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.viewmodel.RoomFormState

@Composable
fun AdminDashboard(
    navController: NavController,
    viewModel: RankingViewModel = hiltViewModel()
) {
    val ranking by viewModel.ranking.collectAsState()
    val rooms by viewModel.rooms.collectAsState()
    val formState by viewModel.formState.collectAsState()

    AdminDashboardContent(
        ranking = ranking,
        rooms = rooms,
        formState = formState,
        onRegistrarSalon = viewModel::registrarSalon,
        onRegistrarLineaBase = viewModel::registrarLineaBase,
        onResetForm = viewModel::resetFormState,
        onDesactivarSalon = viewModel::desactivarSalon,
        navController = navController
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardContent(
    ranking: List<Course>,
    rooms: List<Room>,
    formState: RoomFormState,
    onRegistrarSalon: (String, String) -> Unit,
    onRegistrarLineaBase: (BaselineDiagnostic) -> Unit,
    onResetForm: () -> Unit,
    onDesactivarSalon: (String) -> Unit,
    navController: NavController
) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = EcoColors.MintBackground,
        topBar = {
            Surface(
                color = EcoColors.AdminPrimary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 48.dp, bottom = 24.dp, start = 16.dp, end = 16.dp)
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
                            "Hola, Administrador",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Panel de control",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    IconButton(onClick = { /* Notificaciones */ }) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Notificaciones",
                            tint = Color.White
                        )
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = EcoColors.AdminPrimary
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("Inicio") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EcoColors.AdminPrimary,
                        selectedTextColor = EcoColors.AdminPrimary,
                        indicatorColor = EcoColors.AdminPrimary.copy(alpha = 0.1f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("Reportes") },
                    icon = { Icon(Icons.Default.Assignment, contentDescription = "Reportes") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EcoColors.AdminPrimary,
                        selectedTextColor = EcoColors.AdminPrimary,
                        indicatorColor = EcoColors.AdminPrimary.copy(alpha = 0.1f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    label = { Text("Notificaciones") },
                    icon = { 
                        BadgedBox(badge = { Badge { Text("3") } }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notificaciones")
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EcoColors.AdminPrimary,
                        selectedTextColor = EcoColors.AdminPrimary,
                        indicatorColor = EcoColors.AdminPrimary.copy(alpha = 0.1f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    label = { Text("Perfil") },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EcoColors.AdminPrimary,
                        selectedTextColor = EcoColors.AdminPrimary,
                        indicatorColor = EcoColors.AdminPrimary.copy(alpha = 0.1f)
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
                0 -> TabInicio(ranking = ranking, totalSalones = rooms.size, navController = navController)
                1 -> TabRankingCompleto(ranking = ranking)
                2 -> ComingSoonScreen("Notificaciones")
                3 -> ComingSoonScreen("Perfil")
            }
        }
    }
}

// ==========================================
// APARTADO 1: INICIO
// ==========================================
@Composable
fun TabInicio(ranking: List<Course>, totalSalones: Int, navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Módulos de Gestión",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EcoColors.TextDark
                )
                ManagementGrid(navController)
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Resumen de Impacto",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EcoColors.TextDark
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AdminSummaryCard(modifier = Modifier.weight(1f), title = "Usuarios", value = "156", icon = Icons.Default.Groups)
                    AdminSummaryCard(modifier = Modifier.weight(1f), title = "Cursos", value = "28", icon = Icons.Default.Class)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AdminSummaryCard(modifier = Modifier.weight(1f), title = "Salones", value = "$totalSalones", icon = Icons.Default.MeetingRoom)
                    AdminSummaryCard(modifier = Modifier.weight(1f), title = "Evaluaciones", value = "1,248", icon = Icons.Default.Analytics)
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Resumen general",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = EcoColors.TextDark
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Simple chart placeholder using Canvas
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxWidth().height(150.dp).padding(vertical = 8.dp)) {
                        val points = listOf(0.4f, 0.6f, 0.55f, 0.75f, 0.65f, 0.85f, 0.9f)
                        val stepX = size.width / (points.size - 1)
                        val path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(0f, size.height * (1 - points[0]))
                            points.forEachIndexed { index, p ->
                                lineTo(index * stepX, size.height * (1 - p))
                            }
                        }
                        drawPath(path, color = EcoColors.AdminPrimary, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx()))
                        points.forEachIndexed { index, p ->
                            drawCircle(color = EcoColors.AdminPrimary, radius = 5.dp.toPx(), center = androidx.compose.ui.geometry.Offset(index * stepX, size.height * (1 - p)))
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { /* Ver más */ }, modifier = Modifier.align(Alignment.End)) {
                        Text("Ver más estadísticas →", color = EcoColors.AdminPrimary)
                    }
                }
            }
        }

        if (ranking.isNotEmpty()) {
            item {
                Text(
                    "Salón Líder",
                    style = MaterialTheme.typography.titleLarge,
                    color = EcoColors.TextDark,
                    modifier = Modifier.padding(top = 8.dp)
                )
                // ... rest of the existing leader card
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = EcoColors.AdminPrimary
                        )
                        Column {
                            Text(
                                ranking.first().nombre,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge,
                                color = EcoColors.TextDark
                            )
                            Text(
                                "${ranking.first().puntosTotales} puntos acumulados",
                                style = MaterialTheme.typography.bodyMedium,
                                color = EcoColors.TextMuted
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
                color = EcoColors.TextDark,
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
            item { SliderIndicator("Estado de limpieza", Icons.Default.CleaningServices, limpieza, EcoColors.AdminPrimary) { limpieza = it } }
            item { SliderIndicator("Clasificación de residuos", Icons.Default.Recycling, clasificacionResiduos, EcoColors.AdminPrimary) { clasificacionResiduos = it } }
            item { SliderIndicator("Ahorro de energía", Icons.Default.Bolt, ahorroEnergia, EcoColors.AdminPrimary) { ahorroEnergia = it } }
            item { SliderIndicator("Cuidado del mobiliario", Icons.Default.Chair, cuidadoMobiliario, EcoColors.AdminPrimary) { cuidadoMobiliario = it } }
            item { SliderIndicator("Participación ambiental", Icons.Default.VolunteerActivism, participacionAmbiental, EcoColors.AdminPrimary) { participacionAmbiental = it } }
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
                    color = EcoColors.AdminPrimary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    onRegistrarSalon(nombreSalon, bloqueSalon)
                },
                enabled = formState !is RoomFormState.Loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = EcoColors.AdminPrimary,
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (formState is RoomFormState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        color = Color.White,
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
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
                ) {
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
fun AdminSummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(EcoColors.AdminPrimary.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = EcoColors.AdminPrimary, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, style = MaterialTheme.typography.labelSmall, color = EcoColors.TextMuted)
                Text(
                    value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = EcoColors.TextDark
                )
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
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        ListItem(
            colors = ListItemDefaults.colors(containerColor = Color.White),
            leadingContent = {
                Icon(
                    Icons.Default.MeetingRoom,
                    contentDescription = null,
                    tint = EcoColors.AdminPrimary
                )
            },
            headlineContent = { Text(course.nombre, fontWeight = FontWeight.SemiBold, color = EcoColors.TextDark) },
            supportingContent = { Text("Puntos acumulados: ${course.puntosTotales}", color = EcoColors.TextMuted) },
            trailingContent = {
                Text(
                    "#${course.posicionRanking}",
                    style = MaterialTheme.typography.titleLarge,
                    color = EcoColors.AdminPrimary
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
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null, tint = primaryColor)
            Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            Text("${value.toInt()}/10", fontWeight = FontWeight.Bold, color = primaryColor)
        }
        Slider(
            value = value, 
            onValueChange = onValueChange, 
            valueRange = 0f..10f, 
            steps = 9,
            colors = SliderDefaults.colors(
                thumbColor = primaryColor,
                activeTrackColor = primaryColor,
                inactiveTrackColor = primaryColor.copy(alpha = 0.24f)
            )
        )
    }
}

@Composable
fun ManagementGrid(navController: NavController) {
    val modules = listOf(
        ModuleItem("Usuarios", Icons.Default.People, Screen.UserManagement.route),
        ModuleItem("Cursos", Icons.Default.Class, Screen.CourseManagement.route),
        ModuleItem("Salones", Icons.Default.MeetingRoom, Screen.RoomManagement.route),
        ModuleItem("Indicadores", Icons.Default.Analytics, Screen.IndicatorManagement.route),
        ModuleItem("Campañas", Icons.Default.Campaign, Screen.CampaignManagement.route),
        ModuleItem("Retos", Icons.Default.EmojiEvents, Screen.ChallengeManagement.route),
        ModuleItem("Insignias", Icons.Default.MilitaryTech, Screen.BadgeManagement.route),
        ModuleItem("Evaluaciones", Icons.Default.FactCheck, Screen.EvaluationManagement.route),
        ModuleItem("Evidencias", Icons.Default.PhotoLibrary, Screen.EvidenceManagement.route),
        ModuleItem("Reportes", Icons.Default.Description, Screen.Reports.route),
        ModuleItem("Ajustes", Icons.Default.Settings, Screen.Settings.route)
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        modules.chunked(2).forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowItems.forEach { module ->
                    ManagementButton(
                        modifier = Modifier.weight(1f),
                        title = module.title,
                        icon = module.icon,
                        onClick = { navController.navigate(module.route) }
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

data class ModuleItem(val title: String, val icon: ImageVector, val route: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagementButton(modifier: Modifier, title: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null, tint = EcoColors.AdminPrimary, modifier = Modifier.size(20.dp))
            Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = EcoColors.TextDark)
        }
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

// ==========================================
// PREVIEW
// ==========================================
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminDashboardPreview() {
    MyApplicationTheme {
        Surface {
            AdminDashboardContent(
                ranking = listOf(
                    Course(id = "1", nombre = "11-01", puntosTotales = 450, posicionRanking = 1),
                    Course(id = "2", nombre = "11-02", puntosTotales = 420, posicionRanking = 2)
                ),
                rooms = listOf(
                    Room(id = "1", nombre = "11-01", bloque = "Bloque A"),
                    Room(id = "2", nombre = "11-02", bloque = "Bloque A")
                ),
                formState = RoomFormState.Idle,
                onRegistrarSalon = { _, _ -> },
                onRegistrarLineaBase = {},
                onResetForm = {},
                onDesactivarSalon = {},
                navController = NavController(androidx.compose.ui.platform.LocalContext.current)
            )
        }
    }
}