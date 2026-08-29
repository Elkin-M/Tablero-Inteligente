package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.EcoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GlobalNotificationViewModel @Inject constructor(
    private val repository: EcoRepository
) : ViewModel() {

    private val _currentAlert = MutableStateFlow<String?>(null)
    val currentAlert: StateFlow<String?> = _currentAlert.asStateFlow()

    private var lastTipId: String? = null
    private var lastEvalId: String? = null
    private var isFirstTipsLoad = true
    private var isFirstEvalLoad = true

    init {
        observeTips()
        observeEvaluations()
    }

    private fun observeTips() {
        viewModelScope.launch {
            repository.getTipsFlow().collect { tips ->
                // Consideramos el más reciente el que tenga la fecha mayor
                val newestTip = tips.maxByOrNull { it.fecha }
                if (newestTip != null) {
                    if (!isFirstTipsLoad && newestTip.id != lastTipId) {
                        showAlert("💡 Nuevo Tip: ${newestTip.contenido}")
                    }
                    lastTipId = newestTip.id
                }
                isFirstTipsLoad = false
            }
        }
    }

    private fun observeEvaluations() {
        viewModelScope.launch {
            repository.getEvaluationsFlow().collect { evals ->
                val newestEval = evals.maxByOrNull { it.fecha }
                if (newestEval != null) {
                    if (!isFirstEvalLoad && newestEval.id != lastEvalId) {
                        showAlert("📝 Nueva evaluación registrada: ${newestEval.puntajeObtenido} pts")
                    }
                    lastEvalId = newestEval.id
                }
                isFirstEvalLoad = false
            }
        }
    }

    private fun showAlert(message: String) {
        viewModelScope.launch {
            _currentAlert.value = message
            delay(7000)
            if (_currentAlert.value == message) {
                _currentAlert.value = null
            }
        }
    }
    
    fun clearAlert() {
        _currentAlert.value = null
    }
}
