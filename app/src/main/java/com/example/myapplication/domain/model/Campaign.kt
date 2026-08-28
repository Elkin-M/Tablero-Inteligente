package com.example.myapplication.domain.model

data class Campaign(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val fechaInicio: Long = System.currentTimeMillis(),
    val fechaFin: Long = System.currentTimeMillis(),
    val activa: Boolean = true
)
