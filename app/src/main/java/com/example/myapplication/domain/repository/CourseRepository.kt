package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.Course
import kotlinx.coroutines.flow.Flow

interface CourseRepository {
    fun getCourses(): Flow<List<Course>>
    fun getRanking(): Flow<List<Course>>
    fun getCourseById(id: String): Flow<Course?>
    suspend fun updateCoursePoints(courseId: String, points: Int): Result<Unit>
}
