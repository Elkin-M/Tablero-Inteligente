package com.example.myapplication.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.myapplication.domain.model.UserRole

/**
 * Paleta de marca de EcoLibertad IA y colores por rol.
 */
object EcoColors {
    // Paleta Base - Verdes Ecológicos
    val PrimaryGreen = Color(0xFF2E7D32)
    val SecondaryGreen = Color(0xFF1B5E20)
    val AccentGreen = Color(0xFF66BB6A)
    val DarkGreen = Color(0xFF1B5E20)
    
    // Superficies y Fondos (Consistentes)
    val MintBackground = Color(0xFFF8FBF9) // Más neutro y limpio
    val AdminBackground = Color(0xFFF3E5F5) // Morado claro para el fondo de Admin
    val SurfaceWhite = Color(0xFFFFFFFF)
    
    // Texto y Contraste
    val TextDark = Color(0xFF1B2B22)
    val TextMuted = Color(0xFF5C6B62) // Mejor contraste para legibilidad
    val Divider = Color(0xFFDCE5E0)
    val Placeholder = Color(0xFF8A9A90) // Color específico para placeholders

    // Roles - Paleta Armonizada
    // Mantenemos la identidad pero ajustamos saturación para sinergia
    val DocentePrimary = Color(0xFF388E3C)
    val DocenteSecondary = Color(0xFF2E7D32)

    val EstudiantePrimary = Color(0xFF0288D1) // Azulito
    val EstudianteSecondary = Color(0xFF01579B)

    val ComitePrimary = Color(0xFF4CAF50) // Verdecito
    val ComiteSecondary = Color(0xFF2E7D32)

    val DirectivoPrimary = Color(0xFF1565C0)
    val DirectivoSecondary = Color(0xFF0D47A1)

    val AdminPrimary = Color(0xFF7E57C2) // Moradito
    val AdminSecondary = Color(0xFF512DA8)

    /**
     * Retorna el color principal según el rol.
     */
    fun getPrimaryColor(role: UserRole): Color = when (role) {
        UserRole.ADMIN -> AdminPrimary
        UserRole.DOCENTE -> DocentePrimary
        UserRole.ESTUDIANTE -> EstudiantePrimary
        UserRole.COMITE_AMBIENTAL -> ComitePrimary
        else -> PrimaryGreen
    }

    /**
     * Retorna el color secundario según el rol.
     */
    fun getSecondaryColor(role: UserRole): Color = when (role) {
        UserRole.ADMIN -> AdminSecondary
        UserRole.DOCENTE -> DocenteSecondary
        UserRole.ESTUDIANTE -> EstudianteSecondary
        UserRole.COMITE_AMBIENTAL -> ComiteSecondary
        else -> SecondaryGreen
    }
}
