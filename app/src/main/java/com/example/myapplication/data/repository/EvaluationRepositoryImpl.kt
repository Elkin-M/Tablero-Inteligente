package com.example.myapplication.data.repository

import android.net.Uri
import com.example.myapplication.domain.model.Evaluation
import com.example.myapplication.domain.repository.EvaluationRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class EvaluationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context
) : EvaluationRepository {

    private val DRIVE_BRIDGE_URL = "https://script.google.com/macros/s/AKfycbxyv3-zF5VjKEHkTAvGFypa5ISLBdOhX-4EECQVrFjRoSHk9W5zi3yU2bhAU7vFmbT2jg/exec"
    
    private val client = okhttp3.OkHttpClient.Builder()
        .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    override suspend fun registerEvaluation(evaluation: Evaluation, photoUris: List<String>): Result<Unit> = try {
        val uploadedUrls = photoUris.mapIndexedNotNull { index, uriString ->
            uploadToDriveBridge(uriString, "evidencia_${evaluation.roomId}_$index")
        }

        if (photoUris.isNotEmpty() && uploadedUrls.isEmpty()) {
            throw Exception("No se pudieron subir las imágenes de evidencia. Verifica tu conexión.")
        }

        val finalEvaluation = evaluation.copy(evidenciasUrls = uploadedUrls)
        
        firestore.runTransaction { transaction ->
            // 1. All Reads
            val courseRef = if (evaluation.courseId.isNotEmpty()) firestore.collection("courses").document(evaluation.courseId) else null
            val roomRef = if (evaluation.roomId.isNotEmpty()) firestore.collection("rooms").document(evaluation.roomId) else null
            
            var courseSnapshot: com.google.firebase.firestore.DocumentSnapshot? = null
            var roomSnapshot: com.google.firebase.firestore.DocumentSnapshot? = null
            
            if (courseRef != null) {
                courseSnapshot = transaction.get(courseRef)
            }
            if (roomRef != null && (courseSnapshot == null || !courseSnapshot.exists())) {
                roomSnapshot = transaction.get(roomRef)
            }
            
            // 2. All Writes
            val evalRef = firestore.collection("evaluations").document()
            transaction.set(evalRef, finalEvaluation.copy(id = evalRef.id))
            
            if (courseSnapshot != null && courseSnapshot.exists()) {
                val currentPoints = courseSnapshot.getLong("puntosTotales") ?: 0L
                transaction.update(courseSnapshot.reference, "puntosTotales", currentPoints + evaluation.puntajeObtenido)
            } else if (roomSnapshot != null && roomSnapshot.exists()) {
                val currentPoints = roomSnapshot.getLong("puntosTotales") ?: 0L
                transaction.update(roomSnapshot.reference, "puntosTotales", currentPoints + evaluation.puntajeObtenido)
            }
        }.await()
        
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    private suspend fun uploadToDriveBridge(uriString: String, fileName: String): String? {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val uri = Uri.parse(uriString)
                val bytes = com.example.myapplication.util.ImageUtils.compressImage(context, uri) ?: return@withContext null
                val base64Image = android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
                
                val json = org.json.JSONObject().apply {
                    put("base64Image", base64Image)
                    put("mimeType", "image/jpeg")
                    put("fileName", "$fileName.jpg")
                }

                val request = okhttp3.Request.Builder()
                    .url(DRIVE_BRIDGE_URL)
                    .post(json.toString().toRequestBody("application/json".toMediaTypeOrNull()))
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return@withContext null
                    val bodyString = response.body?.string() ?: ""
                    val resJson = org.json.JSONObject(bodyString)
                    if (resJson.optString("status") == "success") {
                        resJson.getString("url")
                    } else null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    override fun getEvaluationsByCourse(courseId: String): Flow<List<Evaluation>> = callbackFlow {
        val subscription = firestore.collection("evaluations")
            .whereEqualTo("courseId", courseId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val evaluations = snapshot?.documents?.mapNotNull { doc ->
                    mapDocumentToEvaluation(doc)
                } ?: emptyList()
                trySend(evaluations)
            }
        awaitClose { subscription.remove() }
    }

    override fun getEvaluationsByRoom(roomId: String): Flow<List<Evaluation>> = callbackFlow {
        val subscription = firestore.collection("evaluations")
            .whereEqualTo("roomId", roomId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val evaluations = snapshot?.documents?.mapNotNull { doc ->
                    mapDocumentToEvaluation(doc)
                } ?: emptyList()
                trySend(evaluations)
            }
        awaitClose { subscription.remove() }
    }

    override fun getEvaluationsByDocente(docenteId: String): Flow<List<Evaluation>> = callbackFlow {
        val subscription = firestore.collection("evaluations")
            .whereEqualTo("docenteId", docenteId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val evaluations = snapshot?.documents?.mapNotNull { doc ->
                    mapDocumentToEvaluation(doc)
                } ?: emptyList()
                trySend(evaluations)
            }
        awaitClose { subscription.remove() }
    }

    private fun mapDocumentToEvaluation(doc: com.google.firebase.firestore.DocumentSnapshot): Evaluation? {
        return try {
            val data = doc.data ?: return null
            Evaluation(
                id = doc.id,
                roomId = data["roomId"] as? String ?: "",
                docenteId = data["docenteId"] as? String ?: "",
                courseId = data["courseId"] as? String ?: "",
                fecha = (data["fecha"] as? Long) ?: 0L,
                puntajeObtenido = (data["puntajeObtenido"] as? Long)?.toInt() ?: 0,
                evidenciasUrls = (data["evidenciasUrls"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                indicadores = (data["indicadores"] as? Map<*, *>)?.entries?.associate { 
                    it.key.toString() to ((it.value as? Long)?.toInt() ?: 0)
                } ?: emptyMap(),
                observaciones = data["observaciones"] as? String ?: ""
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
