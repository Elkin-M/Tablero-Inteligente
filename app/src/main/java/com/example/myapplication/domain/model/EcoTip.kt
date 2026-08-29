package com.example.myapplication.domain.model

data class EcoTip(
    val id: String = "",
    val contenido: String = "",
    val autor: String = "Admin",
    val activa: Boolean = true,
    val fecha: Long = 0L
)
