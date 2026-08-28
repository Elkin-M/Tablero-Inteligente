package com.example.myapplication.data.remote

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.api.client.http.ByteArrayContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import java.io.IOException
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Helper class to interact with Google Drive via the Google Drive API.
 */
class DriveServiceHelper(private val mDriveService: Drive) {
    private val mExecutor: Executor = Executors.newSingleThreadExecutor()

    /**
     * Creates a text file in the user's Google Drive and returns its file ID.
     */
    fun createFile(fileName: String, content: String): Task<String> {
        return Tasks.call(mExecutor) {
            val metadata = File()
                .setName(fileName)
                .setMimeType("application/json")

            val contentStream = ByteArrayContent.fromString("application/json", content)

            val googleFile = mDriveService.files().create(metadata, contentStream).execute()
                ?: throw IOException("Null result when requesting file creation.")

            googleFile.id
        }
    }

    /**
     * Reads the content of a file given its ID.
     */
    fun readFile(fileId: String): Task<String> {
        return Tasks.call(mExecutor) {
            mDriveService.files().get(fileId).executeMediaAsInputStream().use { inputStream ->
                inputStream.bufferedReader().use { it.readText() }
            }
        }
    }

    /**
     * Updates the content of an existing file.
     */
    fun updateFile(fileId: String, content: String): Task<Void?> {
        return Tasks.call(mExecutor) {
            val contentStream = ByteArrayContent.fromString("application/json", content)
            mDriveService.files().update(fileId, null, contentStream).execute()
            null
        }
    }

    /**
     * Lists files with a specific name to check if the "database" already exists.
     */
    fun searchFile(fileName: String): Task<String?> {
        return Tasks.call(mExecutor) {
            val result = mDriveService.files().list()
                .setQ("name = '$fileName' and trashed = false")
                .setSpaces("drive")
                .execute()

            if (result.files.isNotEmpty()) {
                result.files[0].id
            } else {
                null
            }
        }
    }
}
