package com.example.myapplication.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object TeacherDashboard : Screen("teacher_dashboard")
    object StudentDashboard : Screen("student_dashboard")
    object AdminDashboard : Screen("admin_dashboard")
    object EvaluationForm : Screen("evaluation_form/{roomId}") {
        fun createRoute(roomId: String) = "evaluation_form/$roomId"
    }
}
