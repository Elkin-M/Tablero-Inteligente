package com.example.myapplication.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import androidx.compose.ui.graphics.Color
import com.example.myapplication.domain.model.UserRole
import com.example.myapplication.ui.theme.EcoColors

@Composable
fun ComingSoonScreen(roleName: String) {
    val role = when (roleName) {
        "Administrador", "Directivo", "Comité Ambiental" -> UserRole.ADMIN
        "Docente" -> UserRole.DOCENTE
        "Estudiante" -> UserRole.ESTUDIANTE
        else -> UserRole.INVITADO
    }
    val primaryColor = EcoColors.getPrimaryColor(role)

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Construction, 
            contentDescription = null, 
            modifier = Modifier.size(64.dp),
            tint = primaryColor
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Panel de $roleName", 
            style = MaterialTheme.typography.titleLarge, 
            fontWeight = FontWeight.Bold,
            color = primaryColor
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Este módulo está en construcción.", style = MaterialTheme.typography.bodyMedium)
    }
}