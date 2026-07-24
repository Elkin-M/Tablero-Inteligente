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
import com.example.myapplication.domain.model.Evaluation
import com.example.myapplication.ui.theme.EcoColors
import com.example.myapplication.ui.viewmodel.EvaluationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvaluationForm(
    roomId: String,
    navController: NavController,
    viewModel: EvaluationViewModel = hiltViewModel()
) {
    var limpieza by remember { mutableFloatStateOf(3f) }
    var reciclaje by remember { mutableFloatStateOf(3f) }
    var energia by remember { mutableStateOf(false) }
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
        val totalPoints = (limpieza.toInt() + reciclaje.toInt() + (if (energia) 5 else 0))
        val evaluation = Evaluation(
            roomId = roomId,
            puntajeObtenido = totalPoints,
            indicadores = mapOf(
                "limpieza" to limpieza.toInt(),
                "reciclaje" to reciclaje.toInt(),
                "ahorro_energia" to (if (energia) 5 else 0)
            )
        )
        viewModel.submitEvaluation(evaluation, selectedImages.map { it.toString() })
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Evaluación: $roomId", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EcoColors.DocentePrimary
                ),
                actions = {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            Text("Indicadores Ambientales", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            // Indicador de Limpieza
            Column {
                Text("Limpieza y Orden: ${limpieza.toInt()}/5", color = EcoColors.DocentePrimary, fontWeight = FontWeight.Bold)
                Slider(
                    value = limpieza,
                    onValueChange = { limpieza = it },
                    valueRange = 1f..5f,
                    steps = 3,
                    enabled = !isLoading,
                    colors = SliderDefaults.colors(
                        thumbColor = EcoColors.DocentePrimary,
                        activeTrackColor = EcoColors.DocentePrimary
                    )
                )
            }

            // Indicador de Reciclaje
            Column {
                Text("Uso correcto de papeleras: ${reciclaje.toInt()}/5", color = EcoColors.DocentePrimary, fontWeight = FontWeight.Bold)
                Slider(
                    value = reciclaje,
                    onValueChange = { reciclaje = it },
                    valueRange = 1f..5f,
                    steps = 3,
                    enabled = !isLoading,
                    colors = SliderDefaults.colors(
                        thumbColor = EcoColors.DocentePrimary,
                        activeTrackColor = EcoColors.DocentePrimary
                    )
                )
            }

            // Ahorro de Energía
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Luces/Ventiladores apagados", fontWeight = FontWeight.Medium)
                Switch(
                    checked = energia, 
                    onCheckedChange = { energia = it }, 
                    enabled = !isLoading,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = EcoColors.DocentePrimary,
                        checkedTrackColor = EcoColors.DocentePrimary.copy(alpha = 0.5f)
                    )
                )
            }

            // Evidencia Fotográfica
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Evidencia Fotográfica", style = MaterialTheme.typography.titleMedium)
                
                if (selectedImages.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.height(100.dp)
                    ) {
                        items(selectedImages) { uri ->
                            Box {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                IconButton(
                                    onClick = { selectedImages = selectedImages - uri },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(24.dp)
                                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f), CircleShape)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Eliminar", modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                OutlinedCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { galleryLauncher.launch("image/*") },
                    enabled = !isLoading
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(48.dp))
                        Text("Agregar Imágenes")
                    }
                }
            }

            // Observaciones
            OutlinedTextField(
                value = observaciones,
                onValueChange = { observaciones = it },
                label = { Text("Observaciones adicionales") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                enabled = !isLoading
            )

            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = EcoColors.DocentePrimary
                )
            ) {
                if (isLoading) {
                    Text("Guardando...")
                } else {
                    Text("Finalizar Evaluación")
                }
            }
        }
    }
}
