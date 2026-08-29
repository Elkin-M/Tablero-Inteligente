package com.example.myapplication.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.UserRole
import com.example.myapplication.ui.theme.EcoColors

private data class RoleOption(
    val role: UserRole,
    val label: String,
    val icon: ImageVector
)

private val roleOptions = listOf(
    RoleOption(UserRole.ADMIN, "Administrador", Icons.Default.AdminPanelSettings),
    RoleOption(UserRole.COMITE_AMBIENTAL, "Comité Ambiental", Icons.Default.Eco),
    RoleOption(UserRole.ESTUDIANTE, "Estudiante", Icons.Default.Face),
    RoleOption(UserRole.INVITADO, "Invitado", Icons.Default.PersonOutline)
)

@Composable
fun RoleSelectionScreen(
    onRoleSelected: (UserRole) -> Unit,
    onBack: () -> Unit
) {
    RoleSelectionScreenContent(
        onRoleSelected = onRoleSelected,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectionScreenContent(
    onRoleSelected: (UserRole) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Selecciona tu rol", fontWeight = FontWeight.Bold) })
        },
        containerColor = EcoColors.MintBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                "Modo vista previa: entra directo a cada panel sin iniciar sesión. " +
                        "Esto es temporal para pruebas.",
                style = MaterialTheme.typography.bodySmall,
                color = EcoColors.TextMuted,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(roleOptions) { option ->
                    ElevatedCard(
                        onClick = { onRoleSelected(option.role) },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val roleColor = EcoColors.getPrimaryColor(option.role)
                            Icon(
                                imageVector = option.icon,
                                contentDescription = null,
                                tint = roleColor,
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = option.label,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = roleColor,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Volver al inicio de sesión")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoleSelectionScreenPreview() {
    com.example.myapplication.ui.theme.MyApplicationTheme {
        RoleSelectionScreenContent(
            onRoleSelected = {},
            onBack = {}
        )
    }
}
