package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.domain.model.UserRole
import com.example.myapplication.ui.admin.AdminDashboard
import com.example.myapplication.ui.admin.BadgeManagementScreen
import com.example.myapplication.ui.admin.CampaignManagementScreen
import com.example.myapplication.ui.admin.ChallengeManagementScreen
import com.example.myapplication.ui.admin.CourseManagementScreen
import com.example.myapplication.ui.admin.EvaluationManagementScreen
import com.example.myapplication.ui.admin.EvidenceManagementScreen
import com.example.myapplication.ui.admin.IndicatorManagementScreen
import com.example.myapplication.ui.admin.ReportsScreen
import com.example.myapplication.ui.admin.RoomManagementScreen
import com.example.myapplication.ui.admin.SettingsScreen
import com.example.myapplication.ui.admin.UserManagementScreen
import com.example.myapplication.ui.auth.InvitadoDashboard
import com.example.myapplication.ui.auth.LoginScreen
import com.example.myapplication.ui.dashboard.EnvironmentalDashboardScreen
import com.example.myapplication.ui.auth.RegisterScreen
import com.example.myapplication.ui.auth.RoleSelectionScreen
import com.example.myapplication.ui.auth.SplashScreen
import com.example.myapplication.ui.common.ComingSoonScreen
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.student.StudentDashboard
import com.example.myapplication.ui.teacher.EvaluationForm
import com.example.myapplication.ui.teacher.TeacherDashboard
import com.example.myapplication.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                EcoLibertadNavigation()
            }
        }
    }
}

@Composable
fun EcoLibertadNavigation() {
    val navController = rememberNavController()

    fun navigateByRole(role: UserRole) {
        val route = when (role) {
            UserRole.ADMIN -> Screen.AdminDashboard.route
            UserRole.DOCENTE -> Screen.TeacherDashboard.route
            UserRole.ESTUDIANTE -> Screen.StudentDashboard.route
            UserRole.INVITADO -> Screen.InvitadoDashboard.route
        }
        navController.navigate(route) {
            popUpTo(Screen.Login.route) { inclusive = true }
        }
    }

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { role -> navigateByRole(role) },
                onGoToRegister = { navController.navigate(Screen.Register.route) },
                onGoToRoleSelection = { navController.navigate(Screen.RoleSelection.route) }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { role -> navigateByRole(role) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.RoleSelection.route) {
            RoleSelectionScreen(
                onRoleSelected = { role -> navigateByRole(role) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.TeacherDashboard.route) {
            TeacherDashboard(navController)
        }
        composable(Screen.StudentDashboard.route) {
            StudentDashboard(navController)
        }
        composable(Screen.AdminDashboard.route) { AdminDashboard(navController) }
        composable(Screen.InvitadoDashboard.route) { InvitadoDashboard(navController) }
        
        // Rutas de Gestión Administrativa
        composable(Screen.UserManagement.route) { UserManagementScreen(navController) }
        composable(Screen.CourseManagement.route) { CourseManagementScreen(navController) }
        composable(Screen.RoomManagement.route) { RoomManagementScreen(navController) }
        composable(Screen.IndicatorManagement.route) { IndicatorManagementScreen(navController) }
        composable(Screen.CampaignManagement.route) { CampaignManagementScreen(navController) }
        composable(Screen.ChallengeManagement.route) { ChallengeManagementScreen(navController) }
        composable(Screen.BadgeManagement.route) { BadgeManagementScreen(navController) }
        composable(Screen.EvaluationManagement.route) { EvaluationManagementScreen(navController) }
        composable(Screen.EvidenceManagement.route) { EvidenceManagementScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.Reports.route) { ReportsScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.Settings.route) { SettingsScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.EnvironmentalDashboard.route) { EnvironmentalDashboardScreen(navController) }

        composable(
            route = Screen.ComingSoon.route,
            arguments = listOf(androidx.navigation.navArgument("roleName") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val roleName = backStackEntry.arguments?.getString("roleName")?.replace("_", " ") ?: ""
            ComingSoonScreen(roleName)
        }
        composable(
            route = Screen.EvaluationForm.route,
            arguments = listOf(androidx.navigation.navArgument("roomId") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            EvaluationForm(roomId, navController)
        }
    }
}