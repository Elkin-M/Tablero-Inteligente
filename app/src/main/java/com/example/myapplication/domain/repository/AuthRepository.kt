package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.model.UserRole
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getCurrentUser(): Flow<User?>
    suspend fun login(email: String, pass: String): Result<User>
    suspend fun logout()
    suspend fun register(user: User, pass: String): Result<User>
    suspend fun signInWithGoogle(idToken: String): Result<User>
}
