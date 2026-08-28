package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.EcoRepository
import com.example.myapplication.domain.model.Badge
import com.example.myapplication.domain.model.Campaign
import com.example.myapplication.domain.model.Challenge
import com.example.myapplication.domain.model.Course
import com.example.myapplication.domain.model.Evaluation
import com.example.myapplication.domain.model.Indicator
import com.example.myapplication.domain.model.Room
import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.model.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManagementViewModel @Inject constructor(
    private val repository: EcoRepository
) : ViewModel() {

    val users: StateFlow<List<User>> = repository.getUsersFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val courses: StateFlow<List<Course>> = repository.getCoursesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rooms: StateFlow<List<Room>> = repository.getRoomsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val challenges: StateFlow<List<Challenge>> = repository.getChallengesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val campaigns: StateFlow<List<Campaign>> = repository.getCampaignsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val badges: StateFlow<List<Badge>> = repository.getBadgesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val evaluations: StateFlow<List<Evaluation>> = repository.getEvaluationsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val indicators: StateFlow<List<Indicator>> = repository.getIndicatorsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateUserRole(uid: String, newRole: UserRole, courseId: String? = null) {
        viewModelScope.launch {
            repository.updateUserRole(uid, newRole, courseId)
        }
    }

    fun createCourse(nombre: String) {
        viewModelScope.launch {
            repository.createCourse(nombre)
        }
    }

    fun deleteCourse(courseId: String) {
        viewModelScope.launch {
            repository.deleteCourse(courseId)
        }
    }

    fun toggleRoomStatus(room: Room) {
        viewModelScope.launch {
            repository.updateRoom(room.copy(active = !room.active))
        }
    }

    fun createChallenge(titulo: String, descripcion: String, puntos: Int) {
        viewModelScope.launch {
            repository.createChallenge(Challenge(titulo = titulo, descripcion = descripcion, puntos = puntos))
        }
    }

    fun toggleChallengeStatus(challenge: Challenge) {
        viewModelScope.launch {
            repository.updateChallenge(challenge.copy(activo = !challenge.activo))
        }
    }

    fun createCampaign(nombre: String, descripcion: String) {
        viewModelScope.launch {
            repository.createCampaign(Campaign(nombre = nombre, descripcion = descripcion))
        }
    }

    fun createBadge(nombre: String, descripcion: String, puntos: Int) {
        viewModelScope.launch {
            repository.createBadge(Badge(nombre = nombre, descripcion = descripcion, puntosRequeridos = puntos))
        }
    }

    fun createIndicator(nombre: String, descripcion: String, categoria: String, valorMaximo: Int) {
        viewModelScope.launch {
            repository.createIndicator(Indicator(nombre = nombre, descripcion = descripcion, categoria = categoria, valorMaximo = valorMaximo))
        }
    }

    fun toggleIndicatorStatus(indicator: Indicator) {
        viewModelScope.launch {
            repository.updateIndicator(indicator.copy(activo = !indicator.activo))
        }
    }
}
