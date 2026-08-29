package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.EcoRepository
import com.example.myapplication.domain.model.BaselineDiagnostic
import com.example.myapplication.domain.model.Course
import com.example.myapplication.domain.model.Room
import com.example.myapplication.domain.model.Evaluation
import com.example.myapplication.domain.model.EcoTip
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RoomFormState {
    object Idle : RoomFormState()
    object Loading : RoomFormState()
    object Success : RoomFormState()
    data class Error(val message: String) : RoomFormState()
}

@HiltViewModel
class RankingViewModel @Inject constructor(
    private val repository: EcoRepository,
    private val authRepository: com.example.myapplication.domain.repository.AuthRepository
) : ViewModel() {

    private var currentUserId: String = ""

    private val _currentUser = MutableStateFlow<com.example.myapplication.domain.model.User?>(null)
    val currentUser: StateFlow<com.example.myapplication.domain.model.User?> = _currentUser.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { user ->
                _currentUser.value = user
                currentUserId = user?.uid ?: ""
            }
        }
    }

    val ranking: StateFlow<List<Course>> = repository.getRankingFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val evaluations: StateFlow<List<Evaluation>> = repository.getEvaluationsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userEvaluations: StateFlow<List<Evaluation>> = combine(evaluations, currentUser) { evals, user ->
        if (user == null || user.courseId == null) emptyList()
        else evals.filter { it.courseId == user.courseId || (it.roomId.isNotEmpty() && it.roomId == user.courseId) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rooms: StateFlow<List<Room>> = repository.getRoomsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tips: StateFlow<List<EcoTip>> = repository.getTipsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _formState = MutableStateFlow<RoomFormState>(RoomFormState.Idle)
    val formState: StateFlow<RoomFormState> = _formState.asStateFlow()

    fun registrarSalon(nombre: String, bloque: String) {
        if (nombre.isBlank() || bloque.isBlank()) {
            _formState.value = RoomFormState.Error("Completa nombre y bloque del salón")
            return
        }
        viewModelScope.launch {
            _formState.value = RoomFormState.Loading
            val result = repository.createRoom(nombre.trim(), bloque.trim(), currentUserId)
            _formState.value = result.fold(
                onSuccess = { RoomFormState.Success },
                onFailure = { RoomFormState.Error(it.message ?: "Error al registrar el salón") }
            )
        }
    }

    fun registrarLineaBase(diagnostic: BaselineDiagnostic) {
        viewModelScope.launch {
            _formState.value = RoomFormState.Loading
            val result = repository.saveBaseline(diagnostic)
            _formState.value = result.fold(
                onSuccess = { RoomFormState.Success },
                onFailure = { RoomFormState.Error(it.message ?: "Error al guardar línea base") }
            )
        }
    }

    fun toggleRoomStatus(roomId: String, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleRoomStatus(roomId, currentStatus)
        }
    }

    fun eliminarSalon(roomId: String) {
        viewModelScope.launch {
            repository.deleteRoom(roomId)
        }
    }

    fun resetFormState() {
        _formState.value = RoomFormState.Idle
    }
}