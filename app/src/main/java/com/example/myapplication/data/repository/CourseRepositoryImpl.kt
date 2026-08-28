package com.example.myapplication.data.repository

import com.example.myapplication.domain.model.Course
import com.example.myapplication.domain.repository.CourseRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CourseRepository {

    override fun getCourses(): Flow<List<Course>> = callbackFlow {
        val subscription = firestore.collection("courses")
            .orderBy("nombre")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val courses = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Course(
                            id = doc.id,
                            nombre = doc.getString("nombre") ?: "",
                            puntosTotales = (doc.getLong("puntosTotales") ?: 0L).toInt(),
                            embajadorAmbiental = doc.getString("embajadorAmbiental") ?: ""
                        )
                    } catch (e: Exception) { null }
                } ?: emptyList()
                trySend(courses)
            }
        awaitClose { subscription.remove() }
    }

    override fun getRanking(): Flow<List<Course>> = callbackFlow {
        val subscription = firestore.collection("courses")
            .orderBy("puntosTotales", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val courses = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Course(
                            id = doc.id,
                            nombre = doc.getString("nombre") ?: "",
                            puntosTotales = (doc.getLong("puntosTotales") ?: 0L).toInt(),
                            embajadorAmbiental = doc.getString("embajadorAmbiental") ?: ""
                        )
                    } catch (e: Exception) { null }
                } ?: emptyList()
                trySend(courses)
            }
        awaitClose { subscription.remove() }
    }

    override fun getCourseById(id: String): Flow<Course?> = callbackFlow {
        val subscription = firestore.collection("courses").document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) {
                    trySend(null)
                    return@addSnapshotListener
                }
                try {
                    val course = Course(
                        id = snapshot.id,
                        nombre = snapshot.getString("nombre") ?: "",
                        puntosTotales = (snapshot.getLong("puntosTotales") ?: 0L).toInt(),
                        embajadorAmbiental = snapshot.getString("embajadorAmbiental") ?: ""
                    )
                    trySend(course)
                } catch (e: Exception) {
                    trySend(null)
                }
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun updateCoursePoints(courseId: String, points: Int): Result<Unit> = try {
        firestore.collection("courses").document(courseId)
            .update("puntosTotales", com.google.firebase.firestore.FieldValue.increment(points.toLong()))
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
