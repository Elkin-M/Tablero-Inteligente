package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.model.User
import com.example.myapplication.domain.model.UserRole
import com.example.myapplication.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val courseRepository: com.example.myapplication.domain.repository.CourseRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _courses = MutableStateFlow<List<com.example.myapplication.domain.model.Course>>(emptyList())
    val courses = _courses.asStateFlow()

    init {
        loadCourses()
    }

    private fun loadCourses() {
        viewModelScope.launch {
            try {
                courseRepository.getCourses().collect {
                    _courses.value = it
                }
            } catch (e: Exception) {
                // Silently fail for now
            }
        }
    }

    fun login(email: String, pass: String, onSuccess: (UserRole) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.login(email, pass)
                .onSuccess { user ->
                    _user.value = user
                    _isLoading.value = false
                    onSuccess(user.rol)
                }
                .onFailure { e ->
                    _isLoading.value = false
                    _error.value = e.message ?: "Error al iniciar sesión"
                }
        }
    }

    fun register(nombre: String, email: String, pass: String, role: UserRole, courseId: String? = null, onSuccess: (UserRole) -> Unit) {
        if (nombre.isBlank() || email.isBlank() || pass.isBlank()) {
            _error.value = "Por favor, completa todos los campos"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val newUser = User(
                    nombre = nombre.trim(), 
                    email = email.trim(), 
                    rol = role,
                    courseId = courseId
                )
                repository.register(newUser, pass)
                    .onSuccess { user ->
                        _user.value = user
                        _isLoading.value = false
                        onSuccess(user.rol)
                    }
                    .onFailure { e ->
                        _isLoading.value = false
                        _error.value = e.message ?: "Error al crear la cuenta"
                    }
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = "Error durante el registro: ${e.localizedMessage}"
            }
        }
    }

    fun signInWithGoogle(idToken: String, onSuccess: (UserRole) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.signInWithGoogle(idToken)
                .onSuccess { user ->
                    _user.value = user
                    _isLoading.value = false
                    onSuccess(user.rol)
                }
                .onFailure { e ->
                    _isLoading.value = false
                    _error.value = e.message ?: "Error al autenticar con Google"
                }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.logout()
            _user.value = null
            onSuccess()
        }
    }
}
