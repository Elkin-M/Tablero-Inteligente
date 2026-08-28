package com.example.myapplication.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.domain.model.BaselineDiagnostic
import com.example.myapplication.domain.model.Room
import com.example.myapplication.ui.theme.EcoColors
import com.example.myapplication.ui.viewmodel.RankingViewModel
import com.example.myapplication.ui.viewmodel.RoomFormState
import com.example.myapplication.util.QRGenerator
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import android.content.Context
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import java.io.OutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomManagementScreen(
    navController: NavController,
    viewModel: RankingViewModel = hiltViewModel()
) {
    val rooms by viewModel.rooms.collectAsState()
    val formState by viewModel.formState.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedRoomForQR by remember { mutableStateOf<Room?>(null) }
    var showInfoBox by remember { mutableStateOf(true) }
    val context = LocalContext.current

    Scaffold(
        containerColor = EcoColors.MintBackground,
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Aulas (QR)", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EcoColors.AdminPrimary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    viewModel.resetFormState()
                    showAddDialog = true 
                },
                containerColor = EcoColors.AdminPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Aula")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (showInfoBox) {
                InfoBox(
                    onDismiss = { showInfoBox = false },
                    url = "https://ecolibertad-ia.web.app" // Reemplazar con URL real
                )
            }

            if (rooms.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay aulas registradas", color = EcoColors.TextMuted)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(rooms) { room ->
                        RoomItem(
                            room = room,
                            onToggleActive = { viewModel.toggleRoomStatus(room.id, room.active) },
                            onShowQR = { selectedRoomForQR = room },
                            onDelete = { viewModel.eliminarSalon(room.id) }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AddRoomDialog(
                formState = formState,
                onDismiss = { showAddDialog = false },
                onConfirm = { nombre, bloque, diagnostic ->
                    viewModel.registrarSalon(nombre, bloque)
                    diagnostic?.let { viewModel.registrarLineaBase(it) }
                }
            )
        }

        selectedRoomForQR?.let { room ->
            QRDialog(
                room = room,
                onDismiss = { selectedRoomForQR = null },
                onDownload = { bitmap ->
                    saveQRToGallery(context, bitmap, "QR_${room.nombre}_${room.bloque}")
                }
            )
        }
    }
}

@Composable
fun InfoBox(onDismiss: () -> Unit, url: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
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
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
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
fun RoomItem(room: Room, onToggleActive: () -> Unit, onShowQR: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MeetingRoom, contentDescription = null, tint = EcoColors.AdminPrimary)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(room.nombre, fontWeight = FontWeight.Bold, color = EcoColors.TextDark)
                    Text(room.bloque, style = MaterialTheme.typography.bodySmall, color = EcoColors.TextMuted)
                }
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onShowQR) {
                    Icon(Icons.Default.QrCode, contentDescription = "Generar QR", tint = EcoColors.AdminPrimary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                }
                Switch(
                    checked = room.active,
                    onCheckedChange = { onToggleActive() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = EcoColors.AdminPrimary,
                        checkedTrackColor = EcoColors.AdminPrimary.copy(alpha = 0.5f)
                    )
                )
            }
        }
    }
}

@Composable
fun QRDialog(room: Room, onDismiss: () -> Unit, onDownload: (Bitmap) -> Unit) {
    val qrBitmap = remember(room.id) { QRGenerator.generateQRCode(room.id) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Código QR: ${room.nombre}") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                qrBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier.size(200.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Escanea este código para calificar el salón", style = MaterialTheme.typography.bodySmall)
                } ?: Text("Error al generar QR")
            }
        },
        confirmButton = {
            Button(
                onClick = { qrBitmap?.let { onDownload(it) } },
                colors = ButtonDefaults.buttonColors(containerColor = EcoColors.AdminPrimary)
            ) {
                Icon(Icons.Default.Download, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Descargar QR")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        }
    )
}

fun saveQRToGallery(context: Context, bitmap: Bitmap, filename: String) {
    val outputStream: OutputStream?
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$filename.png")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/EcoLibertad")
        }
        val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        outputStream = imageUri?.let { resolver.openOutputStream(it) }
    } else {
        val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
        val image = java.io.File(imagesDir, "$filename.png")
        outputStream = java.io.FileOutputStream(image)
    }

    outputStream?.use {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        Toast.makeText(context, "QR guardado en Galería", Toast.LENGTH_SHORT).show()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRoomDialog(
    formState: RoomFormState,
    onDismiss: () -> Unit,
    onConfirm: (String, String, BaselineDiagnostic?) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var bloque by remember { mutableStateOf("") }
    var includeBaseline by remember { mutableStateOf(false) }
    
    // Baseline state
    var limpieza by remember { mutableStateOf(5f) }
    var residuos by remember { mutableStateOf(5f) }
    var energia by remember { mutableStateOf(5f) }
    var mobiliario by remember { mutableStateOf(5f) }
    var participacion by remember { mutableStateOf(5f) }

    LaunchedEffect(formState) {
        if (formState is RoomFormState.Success) {
            onDismiss()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Nueva Aula") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre de Aula (ej. Laboratorio 1)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = bloque,
                        onValueChange = { bloque = it },
                        label = { Text("Bloque / Ubicación") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Checkbox(checked = includeBaseline, onCheckedChange = { includeBaseline = it })
                        Text("Incluir diagnóstico inicial", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                if (includeBaseline) {
                    item { Text("Diagnóstico Inicial (0-10)", style = MaterialTheme.typography.labelMedium, color = EcoColors.AdminPrimary) }
                    item { SliderIndicator("Limpieza", Icons.Default.CleaningServices, limpieza) { limpieza = it } }
                    item { SliderIndicator("Residuos", Icons.Default.Recycling, residuos) { residuos = it } }
                    item { SliderIndicator("Energía", Icons.Default.Bolt, energia) { energia = it } }
                    item { SliderIndicator("Mobiliario", Icons.Default.Chair, mobiliario) { mobiliario = it } }
                    item { SliderIndicator("Participación", Icons.Default.VolunteerActivism, participacion) { participacion = it } }
                }
                if (formState is RoomFormState.Error) {
                    item {
                        Text(formState.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val diagnostic = if (includeBaseline) {
                        BaselineDiagnostic(
                            roomId = "", // Se asigna en repo
                            estadoLimpieza = limpieza.toInt(),
                            clasificacionResiduos = residuos.toInt(),
                            ahorroEnergia = energia.toInt(),
                            cuidadoMobiliario = mobiliario.toInt(),
                            participacionAmbiental = participacion.toInt()
                        )
                    } else null
                    onConfirm(nombre, bloque, diagnostic)
                },
                enabled = formState !is RoomFormState.Loading,
                colors = ButtonDefaults.buttonColors(containerColor = EcoColors.AdminPrimary)
            ) {
                if (formState is RoomFormState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                } else {
                    Text("Guardar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun SliderIndicator(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = EcoColors.AdminPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
            Text("${value.toInt()}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..10f,
            steps = 9,
            colors = SliderDefaults.colors(thumbColor = EcoColors.AdminPrimary, activeTrackColor = EcoColors.AdminPrimary)
        )
    }
}
