package com.example.myapplication.ui.teacher

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myapplication.domain.model.BaselineDiagnostic
import com.example.myapplication.domain.model.Evaluation
import com.example.myapplication.domain.model.Indicator
import com.example.myapplication.ui.theme.EcoColors
import com.example.myapplication.ui.viewmodel.EvaluationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvaluationForm(
    roomId: String,
    navController: NavController,
    viewModel: EvaluationViewModel = hiltViewModel()
) {
    val indicators by viewModel.indicators.collectAsState()
    val rooms by viewModel.rooms.collectAsState()
    val roomName = remember(rooms, roomId) {
        rooms.find { it.id == roomId }?.nombre ?: roomId
    }

    val indicatorScores = remember { mutableStateMapOf<String, Int>() }
    
    // Sliders de diagnóstico inicial (0-10)
    var limpieza by remember { mutableFloatStateOf(0f) }
    var residuos by remember { mutableFloatStateOf(0f) }
    var energia by remember { mutableFloatStateOf(0f) }
    var mobiliario by remember { mutableFloatStateOf(0f) }
    var participacion by remember { mutableFloatStateOf(0f) }

    var observaciones by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        selectedImages = selectedImages + uris
    }
    
    val isLoading by viewModel.loading.collectAsState()
    val isSuccess by viewModel.success.collectAsState()
    val error by viewModel.error.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            navController.popBackStack()
        }
    }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    val onSave = {
        if (indicatorScores.isEmpty() && indicators.any { it.activo }) {
            // Podríamos mostrar un error, pero mejor inicializamos con valores por defecto
        }
        
        val finalScores = indicators.filter { it.activo }.associate { 
            it.nombre to (indicatorScores[it.id] ?: 0)
        }
        val totalPoints = finalScores.values.sum()
        
        val evaluation = Evaluation(
            roomId = roomId,
            courseId = roomId, 
            puntajeObtenido = totalPoints,
            indicadores = finalScores,
            observaciones = observaciones,
            fecha = System.currentTimeMillis()
        )

        val diagnostic = BaselineDiagnostic(
            roomId = roomId,
            estadoLimpieza = limpieza.toInt(),
            clasificacionResiduos = residuos.toInt(),
            ahorroEnergia = energia.toInt(),
            cuidadoMobiliario = mobiliario.toInt(),
            participacionAmbiental = participacion.toInt()
        )

        viewModel.submitEvaluationWithDiagnostic(evaluation, diagnostic, selectedImages.map { it.toString() })
    }

    Scaffold(
        containerColor = EcoColors.MintBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Evaluación Ambiental", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Salón: $roomName", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "Cancelar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = EcoColors.DocentePrimary
                ),
                actions = {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        IconButton(onClick = onSave) {
                            Icon(Icons.Default.Check, contentDescription = "Guardar", tint = Color.White)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (isLoading) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = EcoColors.DocentePrimary.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Subiendo evidencias y guardando datos...", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Text(
                "Diagnóstico Inicial (0-10)",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = EcoColors.TextDark
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DiagnosticSlider("Estado de Limpieza", limpieza) { limpieza = it }
                    DiagnosticSlider("Clasificación de Residuos", residuos) { residuos = it }
                    DiagnosticSlider("Ahorro de Energía", energia) { energia = it }
                    DiagnosticSlider("Cuidado Mobiliario", mobiliario) { mobiliario = it }
                    DiagnosticSlider("Participación Ambiental", participacion) { participacion = it }
                }
            }

            Text(
                "Indicadores de Desempeño",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = EcoColors.TextDark
            )

            // Renderizado dinámico de indicadores
            indicators.filter { it.activo }.forEach { indicator ->
                val currentScore = indicatorScores[indicator.id] ?: 0
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                indicator.nombre,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = EcoColors.DocentePrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                color = if (currentScore > 0) EcoColors.DocentePrimary else Color.LightGray,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "${currentScore}/${indicator.valorMaximo}",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        
                        if (indicator.descripcion.isNotEmpty()) {
                            Text(
                                indicator.descripcion,
                                style = MaterialTheme.typography.bodySmall,
                                color = EcoColors.TextMuted,
                                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                            )
                        }
                        
                        Slider(
                            value = currentScore.toFloat(),
                            onValueChange = { indicatorScores[indicator.id] = it.toInt() },
                            valueRange = 0f..indicator.valorMaximo.toFloat(),
                            steps = if (indicator.valorMaximo > 1) indicator.valorMaximo - 1 else 0,
                            enabled = !isLoading,
                            colors = SliderDefaults.colors(
                                thumbColor = EcoColors.DocentePrimary,
                                activeTrackColor = EcoColors.DocentePrimary,
                                inactiveTrackColor = EcoColors.DocentePrimary.copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            }

            // Evidencia Fotográfica
            Text(
                "Evidencia Fotográfica",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = EcoColors.TextDark
            )
            
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (selectedImages.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            items(selectedImages) { uri ->
                                Box(modifier = Modifier.size(120.dp)) {
                                    AsyncImage(
                                        model = uri,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(16.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    IconButton(
                                        onClick = { selectedImages = selectedImages - uri },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                            .size(28.dp)
                                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Eliminar", tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EcoColors.DocentePrimary.copy(alpha = 0.1f), contentColor = EcoColors.DocentePrimary)
                    ) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Seleccionar Imágenes", fontWeight = FontWeight.Bold)
                    }
                    
                    Text(
                        "Las fotos ayudan a validar los puntajes asignados.",
                        style = MaterialTheme.typography.bodySmall,
                        color = EcoColors.TextMuted
                    )
                }
            }

            // Observaciones
            Text(
                "Notas Adicionales",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = EcoColors.TextDark
            )
            
            OutlinedTextField(
                value = observaciones,
                onValueChange = { observaciones = it },
                placeholder = { Text("Escribe aquí cualquier detalle relevante...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                enabled = !isLoading,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EcoColors.DocentePrimary,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = EcoColors.DocentePrimary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Finalizar y Guardar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun DiagnosticSlider(label: String, value: Float, onValueChange: (Float) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = EcoColors.TextDark)
            Text(
                "${value.toInt()}/10",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = EcoColors.DocentePrimary
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..10f,
            steps = 9,
            colors = SliderDefaults.colors(
                thumbColor = EcoColors.DocentePrimary,
                activeTrackColor = EcoColors.DocentePrimary,
                inactiveTrackColor = EcoColors.DocentePrimary.copy(alpha = 0.1f)
            )
        )
    }
}
