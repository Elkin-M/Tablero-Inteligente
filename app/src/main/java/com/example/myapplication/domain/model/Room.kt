package com.example.myapplication.domain.model

data class Room(
    val id: String = "", // ID del QR
    val nombre: String = "",
    val bloque: String = "",
    val ultimaEvaluacion: Long? = null
)
