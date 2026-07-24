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

class EvaluationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : EvaluationRepository {

    override suspend fun registerEvaluation(evaluation: Evaluation, photoUris: List<String>): Result<Unit> = try {
        val uploadedUrls = photoUris.map { uriString ->
            val ref = storage.reference.child("evaluations/${UUID.randomUUID()}.jpg")
            ref.putFile(Uri.parse(uriString)).await()
            ref.downloadUrl.await().toString()
        }

        val finalEvaluation = evaluation.copy(evidenciasUrls = uploadedUrls)
        
        firestore.runTransaction { transaction ->
            val evalRef = firestore.collection("evaluations").document()
            transaction.set(evalRef, finalEvaluation.copy(id = evalRef.id))
            
            // Update course points
            val courseRef = firestore.collection("courses").document(evaluation.courseId)
            val courseSnapshot = transaction.get(courseRef)
            val currentPoints = courseSnapshot.getLong("puntosTotales") ?: 0L
            transaction.update(courseRef, "puntosTotales", currentPoints + evaluation.puntajeObtenido)
        }.await()
        
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun getEvaluationsByCourse(courseId: String): Flow<List<Evaluation>> = callbackFlow {
        val subscription = firestore.collection("evaluations")
            .whereEqualTo("courseId", courseId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val evaluations = snapshot?.toObjects(Evaluation::class.java) ?: emptyList()
                trySend(evaluations)
            }
        awaitClose { subscription.remove() }
    }

    override fun getEvaluationsByRoom(roomId: String): Flow<List<Evaluation>> = callbackFlow {
        val subscription = firestore.collection("evaluations")
            .whereEqualTo("roomId", roomId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val evaluations = snapshot?.toObjects(Evaluation::class.java) ?: emptyList()
                trySend(evaluations)
            }
        awaitClose { subscription.remove() }
    }

    override fun getEvaluationsByDocente(docenteId: String): Flow<List<Evaluation>> = callbackFlow {
        val subscription = firestore.collection("evaluations")
            .whereEqualTo("docenteId", docenteId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val evaluations = snapshot?.toObjects(Evaluation::class.java) ?: emptyList()
                trySend(evaluations)
            }
        awaitClose { subscription.remove() }
    }
}
