package com.example.myapplication.data.repository

import com.example.myapplication.data.remote.DriveServiceHelper
import com.example.myapplication.domain.repository.DriveRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DriveRepositoryImpl @Inject constructor(
    private val driveServiceHelper: DriveServiceHelper?
) : DriveRepository {

    private val DATABASE_FILE_NAME = "ecoschool_database.json"

    override suspend fun saveAppData(jsonData: String): Result<Unit> = try {
        if (driveServiceHelper == null) throw Exception("Drive service not initialized")

        val fileId = driveServiceHelper.searchFile(DATABASE_FILE_NAME).await()
        if (fileId != null) {
            driveServiceHelper.updateFile(fileId, jsonData).await()
        } else {
            driveServiceHelper.createFile(DATABASE_FILE_NAME, jsonData).await()
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun loadAppData(): Result<String> = try {
        if (driveServiceHelper == null) throw Exception("Drive service not initialized")

        val fileId = driveServiceHelper.searchFile(DATABASE_FILE_NAME).await()
            ?: throw Exception("Database file not found in Drive")
        
        val content = driveServiceHelper.readFile(fileId).await()
        Result.success(content)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
