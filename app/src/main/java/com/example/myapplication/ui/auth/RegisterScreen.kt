package com.example.myapplication.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.domain.model.UserRole
import com.example.myapplication.ui.theme.EcoColors
import com.example.myapplication.ui.viewmodel.AuthViewModel

private data class RegisterRoleOption(val role: UserRole, val label: String, val icon: ImageVector)

private val registrableRoles = listOf(
    RegisterRoleOption(UserRole.ADMIN, "Administrador", Icons.Default.AdminPanelSettings),
    RegisterRoleOption(UserRole.DOCENTE, "Docente", Icons.Default.School),
    RegisterRoleOption(UserRole.ESTUDIANTE, "Estudiante", Icons.Default.Face)
)

@Composable
fun RegisterScreen(
    onRegisterSuccess: (UserRole) -> Unit,
    onBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.error.collectAsState()

    RegisterScreenContent(
        nombre = nombre,
        onNombreChange = { nombre = it; viewModel.clearError() },
        email = email,
        onEmailChange = { email = it; viewModel.clearError() },
        password = password,
        onPasswordChange = { password = it; viewModel.clearError() },
        passwordVisible = passwordVisible,
        onPasswordVisibilityChange = { passwordVisible = !passwordVisible },
        isLoading = isLoading,
        errorMessage = errorMessage,
        onRegisterClick = {
            viewModel.register(
                nombre = nombre,
                email = email,
                pass = password,
                role = UserRole.INVITADO,
                courseId = null,
                onSuccess = onRegisterSuccess
            )
        },
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreenContent(
    nombre: String,
    onNombreChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    onRegisterClick: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear cuenta", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, enabled = !isLoading) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                modifier = Modifier.statusBarsPadding()
            )
        },
        containerColor = EcoColors.MintBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Únete a la comunidad de EcoLibertad IA",
                style = MaterialTheme.typography.titleMedium,
                color = EcoColors.TextDark
            )
            
            Text(
                "Al registrarte, entrarás como Invitado. Un administrador deberá asignarte un rol específico (Docente o Estudiante) para acceder a todas las funciones.",
                style = MaterialTheme.typography.bodySmall,
                color = EcoColors.TextMuted
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = onNombreChange,
                label = { Text("Nombre completo") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                enabled = !isLoading,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EcoColors.PrimaryGreen,
                    unfocusedBorderColor = EcoColors.Divider
                )
            )

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
                    unfocusedBorderColor = EcoColors.Divider
                )
            )

            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = onPasswordVisibilityChange) {
                        val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        Icon(imageVector = icon, contentDescription = null)
                    }
                },
                singleLine = true,
                enabled = !isLoading,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EcoColors.PrimaryGreen,
                    unfocusedBorderColor = EcoColors.Divider
                )
            )

            if (errorMessage != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        errorMessage,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Button(
                onClick = onRegisterClick,
                enabled = !isLoading && nombre.isNotBlank() && email.isNotBlank() && password.length >= 6,
                colors = ButtonDefaults.buttonColors(containerColor = EcoColors.PrimaryGreen),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Crear cuenta", fontWeight = FontWeight.Bold)
                }
            }

            TextButton(onClick = onBack, enabled = !isLoading, modifier = Modifier.fillMaxWidth()) {
                Text("¿Ya tienes cuenta? Inicia sesión", color = EcoColors.PrimaryGreen)
            }
        }
    }
}
