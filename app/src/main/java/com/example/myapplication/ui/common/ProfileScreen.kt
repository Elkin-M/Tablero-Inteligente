package com.example.myapplication.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.EcoColors
import com.example.myapplication.ui.viewmodel.AuthViewModel

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    val user by authViewModel.user.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(EcoColors.AdminPrimary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = EcoColors.AdminPrimary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = user?.nombre ?: "Usuario",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = EcoColors.TextDark
        )
        Text(
            text = user?.rol?.name ?: "ROL",
            style = MaterialTheme.typography.bodyLarge,
            color = EcoColors.AdminPrimary,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(32.dp))

        ProfileInfoCard(
            icon = Icons.Default.Email,
            label = "Correo Electrónico",
            value = user?.email ?: "No disponible"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ProfileInfoCard(
            icon = Icons.Default.Badge,
            label = "ID de Usuario",
            value = user?.uid ?: "---"
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar Sesión")
        }
    }
}

@Composable
fun ProfileInfoCard(icon: ImageVector, label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = EcoColors.TextMuted)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelSmall, color = EcoColors.TextMuted)
                Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            }
        }
    }
}
