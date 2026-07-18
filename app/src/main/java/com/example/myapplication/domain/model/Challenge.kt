package com.example.myapplication.domain.model

data class Challenge(
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val puntos: Int = 0,
    val activo: Boolean = true,
    val fechaExpiracion: Long? = null
)
