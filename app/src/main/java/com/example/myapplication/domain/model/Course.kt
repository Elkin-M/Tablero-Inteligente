package com.example.myapplication.domain.model

data class Course(
    val id: String = "",
    val nombre: String = "",
    val puntosTotales: Int = 0,
    val posicionRanking: Int = 0,
    val embajadorAmbiental: String = "" // Nombre del estudiante destacado
)
