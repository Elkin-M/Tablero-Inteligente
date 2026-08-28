package com.example.myapplication.domain.repository

interface DriveRepository {
    suspend fun saveAppData(jsonData: String): Result<Unit>
    suspend fun loadAppData(): Result<String>
}
