package com.example.myapplication.domain.model

data class EcoEvent(
    val id: String = "",
    val titulo: String = "",
    val fecha: Long = System.currentTimeMillis(),
    val descripcion: String = "",
    val activa: Boolean = true
)
