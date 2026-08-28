package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.EcoRepository
import com.example.myapplication.domain.model.Evaluation
import com.example.myapplication.domain.model.Indicator
import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.repository.AuthRepository
import com.example.myapplication.domain.repository.EvaluationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EvaluationViewModel @Inject constructor(
    private val repository: EvaluationRepository,
    private val authRepository: AuthRepository,
    private val ecoRepository: EcoRepository
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _success = MutableStateFlow(false)
    val success = _success.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _recentEvaluations = MutableStateFlow<List<Evaluation>>(emptyList())
    val recentEvaluations = _recentEvaluations.asStateFlow()

    val indicators: StateFlow<List<Indicator>> = ecoRepository.getIndicatorsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val courses: StateFlow<List<com.example.myapplication.domain.model.Course>> = ecoRepository.getCoursesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rooms: StateFlow<List<com.example.myapplication.domain.model.Room>> = ecoRepository.getRoomsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val students: StateFlow<List<User>> = ecoRepository.getUsersFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateStudentCourse(studentUid: String, courseId: String) {
        viewModelScope.launch {
            ecoRepository.updateUserRole(studentUid, com.example.myapplication.domain.model.UserRole.ESTUDIANTE, courseId)
        }
    }

    init {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { user ->
                _currentUser.value = user
                user?.let {
                    loadRecentEvaluations(it.uid)
                }
            }
        }
    }

    private fun loadRecentEvaluations(docenteId: String) {
        viewModelScope.launch {
            try {
                repository.getEvaluationsByDocente(docenteId).collect { list ->
                    _recentEvaluations.value = list.sortedByDescending { eval -> eval.fecha }
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar evaluaciones: ${e.message}"
            }
        }
    }

    fun submitEvaluation(evaluation: Evaluation, photoUris: List<String>) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            val user = _currentUser.value
            
            // Si el roomId proporcionado es el ID de un salón/aula, lo usamos como courseId para los puntos
            val finalEval = evaluation.copy(
                docenteId = user?.uid ?: "unknown",
                courseId = evaluation.roomId // Usamos el ID del aula como courseId para la transacción de puntos
            )
            
            val result = repository.registerEvaluation(finalEval, photoUris)
            _loading.value = false
            if (result.isSuccess) {
                _success.value = true
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Error desconocido"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
