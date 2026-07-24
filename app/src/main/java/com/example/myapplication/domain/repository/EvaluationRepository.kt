package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.Evaluation
import kotlinx.coroutines.flow.Flow

interface EvaluationRepository {
    suspend fun registerEvaluation(evaluation: Evaluation, photoUris: List<String>): Result<Unit>
    fun getEvaluationsByCourse(courseId: String): Flow<List<Evaluation>>
    fun getEvaluationsByRoom(roomId: String): Flow<List<Evaluation>>
    fun getEvaluationsByDocente(docenteId: String): Flow<List<Evaluation>>
}
