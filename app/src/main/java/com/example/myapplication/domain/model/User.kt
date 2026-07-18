package com.example.myapplication.domain.model

data class User(
    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val rol: UserRole = UserRole.ESTUDIANTE,
    val courseId: String? = null
)
