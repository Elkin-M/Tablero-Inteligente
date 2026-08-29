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

data class EnvironmentalImpact(
    val totalPoints: Int = 0,
    val totalEvaluations: Int = 0,
    val totalBottles: Int = 0,
    val totalTapas: Int = 0,
    val recentEvidences: List<String> = emptyList(),
    val pointsByCategory: Map<String, Int> = emptyMap()
)

@HiltViewModel
class EnvironmentalDashboardViewModel @Inject constructor(
    private val repository: EcoRepository
) : ViewModel() {

    val impactData: StateFlow<EnvironmentalImpact> = repository.getEvaluationsFlow()
        .map { evaluations ->
            val totalPoints = evaluations.sumOf { it.puntajeObtenido }
            val evidences = evaluations.flatMap { it.evidenciasUrls }.take(10)
            
            var bottles = 0
            var tapas = 0
            val categories = mutableMapOf<String, Int>()
            
            evaluations.forEach { eval ->
                eval.indicadores.forEach { (key, value) ->
                    categories[key] = categories.getOrDefault(key, 0) + value
                    if (key.contains("Botella", ignoreCase = true)) bottles += value
                    if (key.contains("Tapa", ignoreCase = true)) tapas += value
                }
            }

            EnvironmentalImpact(
                totalPoints = totalPoints,
                totalEvaluations = evaluations.size,
                totalBottles = bottles,
                totalTapas = tapas,
                recentEvidences = evidences,
                pointsByCategory = categories
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), EnvironmentalImpact())
}
