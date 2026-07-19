package com.example.myapplication.domain.model

/**
 * Representa un Salón dentro del sistema. Es el único concepto de
 * "unidad evaluable" — no se mezcla con "Curso" en ningún lado de la UI.
 */
data class Room(
    val id: String = "",
    val nombre: String = "",       // Ej: "8A"
    val bloque: String = "",       // Ej: "Bloque Principal"
    val active: Boolean = true,
    val fechaRegistro: Long = System.currentTimeMillis()
)