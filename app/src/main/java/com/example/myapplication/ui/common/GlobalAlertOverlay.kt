package com.example.myapplication.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.UserRole
import com.example.myapplication.ui.theme.EcoColors
import com.example.myapplication.ui.viewmodel.GlobalNotificationViewModel

@Composable
fun GlobalAlertOverlay(
    viewModel: GlobalNotificationViewModel,
    role: UserRole? = null
) {
    val currentAlert by viewModel.currentAlert.collectAsState()
    
    // Determinamos el color de fondo basado en el rol
    val backgroundColor = when (role) {
        UserRole.ESTUDIANTE -> EcoColors.EstudiantePrimary
        UserRole.COMITE_AMBIENTAL, UserRole.DOCENTE -> EcoColors.ComitePrimary
        UserRole.ADMIN -> EcoColors.AdminPrimary
        else -> MaterialTheme.colorScheme.primaryContainer
    }
    
    val contentColor = if (role != null && role != UserRole.INVITADO) {
        Color.White
    } else {
        MaterialTheme.colorScheme.onPrimaryContainer
    }

    AnimatedVisibility(
        visible = currentAlert != null,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -it })
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 85.dp, start = 20.dp, end = 20.dp), // Ajustado para estar justo debajo del navbar
            contentAlignment = Alignment.TopCenter
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.clearAlert() },
                shape = MaterialTheme.shapes.large, // Bordes más redondeados
                color = backgroundColor,
                tonalElevation = 8.dp,
                shadowElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currentAlert ?: "",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.ExtraBold, // Más negrita para resaltar
                            color = contentColor
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
