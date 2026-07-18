package com.example.myapplication.domain.model

data class Evaluation(
    val id: String = "",
    val roomId: String = "",
    val docenteId: String = "",
    val courseId: String = "",
    val fecha: Long = System.currentTimeMillis(),
    val puntajeObtenido: Int = 0,
    val evidenciasUrls: List<String> = emptyList(),
    val indicadores: Map<String, Int> = emptyMap() // Ej: {"limpieza": 5, "energia": 4}
)
