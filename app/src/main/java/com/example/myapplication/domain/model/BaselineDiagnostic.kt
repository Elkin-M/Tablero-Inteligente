package com.example.myapplication.domain.model

/**
 * Diagnóstico inicial de un salón. Se registra UNA sola vez, antes
 * de que empiecen las evaluaciones semanales.
 */
data class BaselineDiagnostic(
    val roomId: String = "",
    val estadoLimpieza: Int = 0,           // 0-10
    val clasificacionResiduos: Int = 0,    // 0-10
    val ahorroEnergia: Int = 0,            // 0-10
    val cuidadoMobiliario: Int = 0,        // 0-10
    val participacionAmbiental: Int = 0,   // 0-10
    val fecha: Long = System.currentTimeMillis()
) {
    val promedioInicial: Double
        get() = listOf(
            estadoLimpieza, clasificacionResiduos, ahorroEnergia,
            cuidadoMobiliario, participacionAmbiental
        ).average()
}