package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.EcoRepository
import com.example.myapplication.domain.model.Course
import com.example.myapplication.domain.model.Evaluation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val repository: EcoRepository
) : ViewModel() {

    val courses: StateFlow<List<Course>> = repository.getCoursesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val evaluations: StateFlow<List<Evaluation>> = repository.getEvaluationsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rooms: StateFlow<List<com.example.myapplication.domain.model.Room>> = repository.getRoomsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val averageScoreByCourse: StateFlow<Map<String, Double>> = combine(courses, evaluations) { courses, evaluations ->
        evaluations.filter { it.puntajeObtenido > 0 }
            .groupBy { it.courseId.ifEmpty { it.roomId } }
            .mapValues { entry ->
                entry.value.map { it.puntajeObtenido }.average()
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun rateEvaluation(evaluationId: String, score: Int) {
        viewModelScope.launch {
            repository.updateEvaluationScore(evaluationId, score)
        }
    }
}
