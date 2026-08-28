package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.EcoRepository
import com.example.myapplication.domain.model.Evaluation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class EvidenceViewModel @Inject constructor(
    private val repository: EcoRepository
) : ViewModel() {

    // Obtenemos solo las evaluaciones que tienen imágenes de evidencia
    val evidenceEvaluations: StateFlow<List<Evaluation>> = repository.getEvaluationsFlow()
        .map { evaluations ->
            evaluations.filter { it.evidenciasUrls.isNotEmpty() }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
