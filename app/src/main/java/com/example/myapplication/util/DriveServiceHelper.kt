package com.example.myapplication.util

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.api.client.http.InputStreamContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import java.io.ByteArrayInputStream
import java.util.Collections
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class DriveServiceHelper(private val mDriveService: Drive) {
    private val mExecutor: Executor = Executors.newSingleThreadExecutor()

    fun uploadFile(fileName: String, mimeType: String, content: ByteArray): Task<String> {
        return Tasks.call(mExecutor) {
            val metadata = File()
                .setName(fileName)
                .setMimeType(mimeType)
                .setParents(Collections.singletonList("root"))

            val contentStream = InputStreamContent(mimeType, ByteArrayInputStream(content))
            val googleFile = mDriveService.files().create(metadata, contentStream).execute()
                ?: throw Exception("Fallo al crear archivo en Drive")

            googleFile.id
        }
    }
}
