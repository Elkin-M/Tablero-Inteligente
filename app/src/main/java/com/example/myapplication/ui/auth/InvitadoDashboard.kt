package com.example.myapplication.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.theme.EcoColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvitadoDashboard(navController: NavController) {
    Scaffold(
        containerColor = EcoColors.MintBackground,
        topBar = {
            TopAppBar(
                title = { Text("EcoLibertad - Invitado", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EcoColors.PrimaryGreen),
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Public,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = EcoColors.PrimaryGreen
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                "¡Bienvenido a EcoLibertad!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = EcoColors.TextDark,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                "Tu cuenta ha sido creada exitosamente. Actualmente tienes el rol de Invitado. Un administrador debe asignarte un rol específico para acceder a todas las funcionalidades.",
                style = MaterialTheme.typography.bodyLarge,
                color = EcoColors.TextMuted,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { navController.navigate(Screen.EnvironmentalDashboard.route) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = EcoColors.PrimaryGreen)
            ) {
                Icon(Icons.Default.Info, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ver Tablero Ambiental")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = { 
                    // Aquí podrías implementar el cierre de sesión si es necesario
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                border = androidx.compose.foundation.BorderStroke(1.dp, EcoColors.PrimaryGreen)
            ) {
                Icon(Icons.Default.Login, contentDescription = null, tint = EcoColors.PrimaryGreen)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión", color = EcoColors.PrimaryGreen)
            }
        }
    }
}
