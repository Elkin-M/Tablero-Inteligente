package com.example.myapplication.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object RoleSelection : Screen("role_selection")
    object TeacherDashboard : Screen("teacher_dashboard")
    object StudentDashboard : Screen("student_dashboard")
    object AdminDashboard : Screen("admin_dashboard")
    
    // Gestión Administrativa
    object UserManagement : Screen("user_management")
    object CourseManagement : Screen("course_management")
    object RoomManagement : Screen("room_management")
    object IndicatorManagement : Screen("indicator_management")
    object CampaignManagement : Screen("campaign_management")
    object ChallengeManagement : Screen("challenge_management")
    object BadgeManagement : Screen("badge_management")
    object EvaluationManagement : Screen("evaluation_management")
    object EvidenceManagement : Screen("evidence_management")
    object Reports : Screen("reports")
    object Settings : Screen("settings")

    object ComingSoon : Screen("coming_soon/{roleName}") {
        fun createRoute(roleName: String) = "coming_soon/$roleName"
    }
    object EvaluationForm : Screen("evaluation_form/{roomId}") {
        fun createRoute(roomId: String) = "evaluation_form/$roomId"
    }
}