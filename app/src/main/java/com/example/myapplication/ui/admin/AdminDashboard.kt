package com.example.myapplication.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.domain.model.Course
import com.example.myapplication.domain.model.Room
import com.example.myapplication.ui.common.ComingSoonScreen
import com.example.myapplication.ui.theme.EcoColors
import com.example.myapplication.ui.viewmodel.RankingViewModel
import com.example.myapplication.ui.viewmodel.AuthViewModel
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.util.QRGenerator
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import com.example.myapplication.ui.common.ProfileScreen

import com.example.myapplication.ui.viewmodel.EnvironmentalDashboardViewModel
import com.example.myapplication.ui.viewmodel.EnvironmentalImpact

@Composable
fun AdminDashboard(
    navController: NavController,
    rankingViewModel: RankingViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    environmentalViewModel: EnvironmentalDashboardViewModel = hiltViewModel()
) {
    val ranking by rankingViewModel.ranking.collectAsState()
    val rooms by rankingViewModel.rooms.collectAsState()
    val impactData by environmentalViewModel.impactData.collectAsState()

    AdminDashboardContent(
        ranking = ranking,
        rooms = rooms,
        impactData = impactData,
        navController = navController,
        authViewModel = authViewModel,
        onLogout = {
            authViewModel.logout {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.AdminDashboard.route) { inclusive = true }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardContent(
    ranking: List<Course>,
    rooms: List<Room>,
    impactData: EnvironmentalImpact,
    navController: NavController,
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = EcoColors.AdminBackground,
        topBar = {
            Surface(
                color = EcoColors.AdminPrimary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 64.dp, bottom = 24.dp, start = 16.dp, end = 16.dp)
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
                    label = { Text("Ranking") },
                    icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Ranking") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EcoColors.AdminPrimary,
                        selectedTextColor = EcoColors.AdminPrimary,
                        indicatorColor = EcoColors.AdminPrimary.copy(alpha = 0.1f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
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
                0 -> TabInicio(ranking = ranking, totalSalones = rooms.size, impactData = impactData, navController = navController)
                1 -> TabRankingCompleto(ranking = ranking)
                2 -> ProfileScreen(authViewModel = authViewModel, onLogout = onLogout)
            }
        }
    }
}

@Composable
fun TabInicio(ranking: List<Course>, totalSalones: Int, impactData: EnvironmentalImpact, navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Módulos de Gestión",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = EcoColors.TextDark
            )
            ManagementGrid(navController)
        }

        item {
            Text(
                "Resumen de Impacto",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = EcoColors.TextDark
            )
            val totalPuntos = remember(ranking) { ranking.sumOf { it.puntosTotales } }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AdminSummaryCard(modifier = Modifier.weight(1f), title = "Salones", value = "$totalSalones", icon = Icons.Default.MeetingRoom)
                    AdminSummaryCard(modifier = Modifier.weight(1f), title = "Puntos Total", value = String.format("%,d", totalPuntos), icon = Icons.Default.Bolt)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AdminSummaryCard(modifier = Modifier.weight(1f), title = "Botellas (Kg)", value = "${impactData.totalBottles}", icon = Icons.Default.Eco)
                    AdminSummaryCard(modifier = Modifier.weight(1f), title = "Tapas (Kg)", value = "${impactData.totalTapas}", icon = Icons.Default.Eco)
                }
            }
        }

        if (ranking.isNotEmpty()) {
            item {
                Text(
                    "Salón Líder",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EcoColors.TextDark
                )
                LeaderCard(course = ranking.first())
            }
        }

        item {
            InfoBox(
                url = "https://tablero-inteligente.web.app/"
            )
        }
    }
}

@Composable
fun InfoBox(url: String) {
    var showInfoBox by remember { mutableStateOf(true) }
    if (!showInfoBox) return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = EcoColors.AdminPrimary.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, EcoColors.AdminPrimary.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = EcoColors.AdminPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Información del Portal", fontWeight = FontWeight.Bold, color = EcoColors.AdminPrimary)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { showInfoBox = false }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = EcoColors.TextMuted)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Accede al tablero web para reportes detallados y análisis de IA.",
                        style = MaterialTheme.typography.bodySmall,
                        color = EcoColors.TextDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        url,
                        style = MaterialTheme.typography.labelSmall,
                        color = EcoColors.AdminPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                val qrBitmap = remember(url) { QRGenerator.generateQRCode(url) }
                qrBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Web QR",
                        modifier = Modifier.size(60.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ManagementGrid(navController: NavController) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ManagementItem(
                modifier = Modifier.weight(1f),
                title = "Usuarios",
                icon = Icons.Default.People,
                color = EcoColors.AdminPrimary,
                onClick = { navController.navigate(Screen.UserManagement.route) }
            )
            ManagementItem(
                modifier = Modifier.weight(1f),
                title = "Aulas (QR)",
                icon = Icons.Default.MeetingRoom,
                color = EcoColors.AdminPrimary,
                onClick = { navController.navigate(Screen.RoomManagement.route) }
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ManagementItem(
                modifier = Modifier.weight(1f),
                title = "Evidencias",
                icon = Icons.Default.PhotoLibrary,
                color = EcoColors.AdminPrimary,
                onClick = { navController.navigate(Screen.EvidenceManagement.route) }
            )
            ManagementItem(
                modifier = Modifier.weight(1f),
                title = "Reportes",
                icon = Icons.Default.Assessment,
                color = EcoColors.AdminPrimary,
                onClick = { navController.navigate(Screen.Reports.route) }
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ManagementItem(
                modifier = Modifier.weight(1f),
                title = "Campañas",
                icon = Icons.Default.Campaign,
                color = EcoColors.AdminPrimary,
                onClick = { navController.navigate(Screen.CampaignManagement.route) }
            )
            ManagementItem(
                modifier = Modifier.weight(1f),
                title = "Eventos",
                icon = Icons.Default.CalendarMonth,
                color = EcoColors.AdminPrimary,
                onClick = { navController.navigate(Screen.EventManagement.route) }
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ManagementItem(
                modifier = Modifier.weight(1f),
                title = "Tips",
                icon = Icons.Default.Lightbulb,
                color = EcoColors.AdminPrimary,
                onClick = { navController.navigate(Screen.TipManagement.route) }
            )
            ManagementItem(
                modifier = Modifier.weight(1f),
                title = "Tablero IA",
                icon = Icons.Default.Public,
                color = EcoColors.AdminPrimary,
                onClick = { navController.navigate(Screen.EnvironmentalDashboard.route) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagementItem(
    modifier: Modifier,
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.labelLarge, color = EcoColors.TextDark)
        }
    }
}

@Composable
fun AdminSummaryCard(modifier: Modifier, title: String, value: String, icon: ImageVector) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = EcoColors.AdminPrimary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, style = MaterialTheme.typography.labelSmall, color = EcoColors.TextMuted)
                Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun LeaderCard(course: Course) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = EcoColors.AdminPrimary),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Salón ${course.nombre}", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("${course.puntosTotales} puntos acumulados", color = Color.White.copy(alpha = 0.9f))
            }
            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(48.dp))
        }
    }
}

@Composable
fun TabRankingCompleto(ranking: List<Course>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("Ranking General", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
        }
        items(ranking.size) { index ->
            val course = ranking[index]
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("#${index + 1}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = EcoColors.AdminPrimary, modifier = Modifier.width(40.dp))
                    Text(course.nombre, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("${course.puntosTotales} pts", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
