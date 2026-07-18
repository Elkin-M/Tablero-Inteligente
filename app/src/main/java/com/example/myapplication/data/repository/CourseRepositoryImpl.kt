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

class CourseRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CourseRepository {

    override fun getRanking(): Flow<List<Course>> = callbackFlow {
        val subscription = firestore.collection("courses")
            .orderBy("puntosTotales", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val courses = snapshot?.toObjects(Course::class.java) ?: emptyList()
                trySend(courses)
            }
        awaitClose { subscription.remove() }
    }

    override fun getCourseById(id: String): Flow<Course?> = callbackFlow {
        val subscription = firestore.collection("courses").document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val course = snapshot?.toObject(Course::class.java)
                trySend(course)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun updateCoursePoints(courseId: String, points: Int): Result<Unit> = try {
        firestore.collection("courses").document(courseId)
            .update("puntosTotales", points)
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
