package com.example.myapplication.domain.model

data class Badge(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val icono: String = "military_tech", // Nombre del icono de Material
    val puntosRequeridos: Int = 0
)
