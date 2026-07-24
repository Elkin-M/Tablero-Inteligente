package com.example.myapplication.ui.auth

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.R
import com.example.myapplication.domain.model.UserRole
import com.example.myapplication.ui.theme.EcoColors
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: (UserRole) -> Unit,
    onGoToRegister: () -> Unit = {},
    onGoToRoleSelection: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.error.collectAsState()

    val context = LocalContext.current

    LoginScreenContent(
        email = email,
        onEmailChange = { email = it; viewModel.clearError() },
        password = password,
        onPasswordChange = { password = it; viewModel.clearError() },
        passwordVisible = passwordVisible,
        onPasswordVisibilityChange = { passwordVisible = !passwordVisible },
        isLoading = isLoading,
        errorMessage = errorMessage,
        onLoginClick = {
            if (email.isBlank() || password.isBlank()) {
                // We could use a local error state or a Toast, but let's keep it consistent
                Toast.makeText(context, "Completa correo y contraseña", Toast.LENGTH_SHORT).show()
                return@LoginScreenContent
            }
            viewModel.login(email.trim(), password, onLoginSuccess)
        },
        onGoogleLoginClick = {
            // Placeholder for Google Sign-In intent/launcher
            Toast.makeText(context, "Iniciando flujo de Google...", Toast.LENGTH_SHORT).show()
            // In a real scenario, you'd launch the Google SignIn intent here
            // and then call viewModel.signInWithGoogle(idToken, onLoginSuccess)
        },
        onGoToRegister = onGoToRegister,
        onGoToRoleSelection = onGoToRoleSelection
    )
}

@Composable
fun LoginScreenContent(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    onLoginClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onGoToRegister: () -> Unit,
    onGoToRoleSelection: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EcoColors.MintBackground)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Box(
            modifier = Modifier
                .size(64.dp)
                .background(EcoColors.PrimaryGreen, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.overlay),
                contentDescription = "Logo Eco",
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "¡Bienvenido!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = EcoColors.TextDark
        )
        Text(
            "Inicia sesión para continuar",
            style = MaterialTheme.typography.bodyMedium,
            color = EcoColors.TextMuted
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Correo electrónico") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            singleLine = true,
            enabled = !isLoading,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EcoColors.PrimaryGreen,
                unfocusedBorderColor = EcoColors.Divider,
                focusedLabelColor = EcoColors.PrimaryGreen,
                unfocusedLabelColor = EcoColors.TextMuted,
                focusedTextColor = EcoColors.TextDark,
                unfocusedTextColor = EcoColors.TextDark,
                focusedLeadingIconColor = EcoColors.PrimaryGreen,
                unfocusedLeadingIconColor = EcoColors.TextMuted
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = onPasswordVisibilityChange) {
                    val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    Icon(imageVector = icon, contentDescription = description)
                }
            },
            singleLine = true,
            enabled = !isLoading,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EcoColors.PrimaryGreen,
                unfocusedBorderColor = EcoColors.Divider,
                focusedLabelColor = EcoColors.PrimaryGreen,
                unfocusedLabelColor = EcoColors.TextMuted,
                focusedTextColor = EcoColors.TextDark,
                unfocusedTextColor = EcoColors.TextDark,
                focusedLeadingIconColor = EcoColors.PrimaryGreen,
                unfocusedLeadingIconColor = EcoColors.TextMuted,
                focusedTrailingIconColor = EcoColors.PrimaryGreen,
                unfocusedTrailingIconColor = EcoColors.TextMuted
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { /* TODO: flujo de recuperación */ },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("¿Olvidaste tu contraseña?", color = EcoColors.PrimaryGreen)
        }

        errorMessage?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onLoginClick,
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = EcoColors.PrimaryGreen,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Iniciar sesión", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = EcoColors.Divider)
            Text(
                "  O continúa con  ",
                color = EcoColors.TextMuted,
                style = MaterialTheme.typography.bodySmall
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = EcoColors.Divider)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onGoogleLoginClick,
            enabled = !isLoading,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                contentColor = EcoColors.TextMuted
            ),
            border = BorderStroke(1.dp, EcoColors.Divider),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Continuar con Google", fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = onGoToRegister, enabled = !isLoading) {
            Text("¿No tienes cuenta? ", color = EcoColors.TextMuted)
            Text("Regístrate", color = EcoColors.PrimaryGreen, fontWeight = FontWeight.Bold)
        }

        TextButton(onClick = onGoToRoleSelection, enabled = !isLoading) {
            Text("Vista previa por rol (sin iniciar sesión)", color = EcoColors.TextMuted)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MyApplicationTheme {
        LoginScreenContent(
            email = "ejemplo@ecolibertad.com",
            onEmailChange = {},
            password = "password",
            onPasswordChange = {},
            passwordVisible = false,
            onPasswordVisibilityChange = {},
            isLoading = false,
            errorMessage = null,
            onLoginClick = {},
            onGoogleLoginClick = {},
            onGoToRegister = {},
            onGoToRoleSelection = {}
        )
    }
}

private fun mapFirestoreRoleToUserRole(roleString: String): UserRole {
    return when (roleString.trim().lowercase()) {
        "admin", "administrador" -> UserRole.ADMIN
        "directivo" -> UserRole.DIRECTIVO
        "docente", "profesor" -> UserRole.DOCENTE
        "comite_ambiental", "comité ambiental", "comite ambiental" -> UserRole.COMITE_AMBIENTAL
        "estudiante", "alumno" -> UserRole.ESTUDIANTE
        else -> UserRole.ESTUDIANTE
    }
}
