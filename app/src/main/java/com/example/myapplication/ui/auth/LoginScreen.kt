package com.example.myapplication.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun LoginScreen(
    onLoginSuccess: (UserRole) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val auth = remember { FirebaseAuth.getInstance() }
    val firestore = remember { FirebaseFirestore.getInstance() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "EcoLibertad IA",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Gestión Ambiental Educativa",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; errorMessage = null },
            label = { Text("Correo Institucional") },
            singleLine = true,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; errorMessage = null },
            label = { Text("Contraseña") },
            singleLine = true,
            enabled = !isLoading,
            visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    errorMessage = "Completa correo y contraseña"
                    return@Button
                }
                isLoading = true
                errorMessage = null

                // 1. Autenticar contra Firebase Auth
                auth.signInWithEmailAndPassword(email.trim(), password)
                    .addOnSuccessListener { authResult ->
                        val uid = authResult.user?.uid
                        if (uid == null) {
                            isLoading = false
                            errorMessage = "No se pudo obtener el usuario"
                            return@addOnSuccessListener
                        }

                        // 2. Buscar el rol en Firestore: users/{uid}
                        firestore.collection("users")
                            .document(uid)
                            .get()
                            .addOnSuccessListener { doc ->
                                isLoading = false
                                if (!doc.exists()) {
                                    errorMessage = "El usuario no tiene perfil registrado"
                                    return@addOnSuccessListener
                                }
                                val roleString = doc.getString("role") ?: ""
                                val role = mapFirestoreRoleToUserRole(roleString)
                                onLoginSuccess(role)
                            }
                            .addOnFailureListener { e ->
                                isLoading = false
                                errorMessage = "Error al leer el perfil: ${e.message}"
                            }
                    }
                    .addOnFailureListener { e ->
                        isLoading = false
                        errorMessage = "Credenciales incorrectas: ${e.message}"
                    }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.height(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Iniciar Sesión")
            }
        }
    }
}

/**
 * Traduce el campo "role" guardado en Firestore (en español, en minúsculas)
 * al enum UserRole que usa la app. Ajusta los strings de la izquierda
 * para que coincidan EXACTAMENTE con lo que guardas en Firestore.
 */
private fun mapFirestoreRoleToUserRole(roleString: String): UserRole {
    return when (roleString.trim().lowercase()) {
        "directivo", "admin", "administrador" -> UserRole.ADMIN
        "docente", "profesor" -> UserRole.DOCENTE
        "estudiante", "alumno" -> UserRole.ESTUDIANTE
        else -> UserRole.ESTUDIANTE
    }
}