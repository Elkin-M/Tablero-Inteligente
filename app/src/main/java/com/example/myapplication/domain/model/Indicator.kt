package com.example.myapplication.domain.model

data class Indicator(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val categoria: String = "",
    val valorMaximo: Int = 5,
    val activo: Boolean = true,
    val esContador: Boolean = false
)
