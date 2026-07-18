package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.Evaluation
import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.repository.AuthRepository
import com.example.myapplication.domain.repository.EvaluationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EvaluationViewModel @Inject constructor(
    private val repository: EvaluationRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _success = MutableStateFlow(false)
    val success = _success.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    init {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect {
                _currentUser.value = it
            }
        }
    }

    fun submitEvaluation(evaluation: Evaluation, photoUris: List<String>) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            // Ensure we have user info
            val user = _currentUser.value
            val finalEval = evaluation.copy(
                docenteId = user?.uid ?: "unknown",
                // In a real scenario, courseId might be linked to the room or selected
                courseId = evaluation.courseId.ifEmpty { user?.courseId ?: "GENERAL" }
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
