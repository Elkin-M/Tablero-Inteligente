package com.example.myapplication

data class EnvironmentalData(
    val label: String,
    val value: String,
    val unit: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val status: String,
    val color: androidx.compose.ui.graphics.Color
)
