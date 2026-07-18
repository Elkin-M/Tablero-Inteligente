package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myapplication.domain.model.Course
import com.example.myapplication.domain.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class RankingViewModel @Inject constructor(
    private val repository: CourseRepository
) : ViewModel() {
    val ranking: Flow<List<Course>> = repository.getRanking()
}
