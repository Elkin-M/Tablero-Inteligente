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
import com.example.myapplication.ui.auth.LoginScreen
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

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(onLoginSuccess = { role ->
                when (role) {
                    UserRole.ADMIN -> navController.navigate(Screen.AdminDashboard.route)
                    UserRole.DOCENTE -> navController.navigate(Screen.TeacherDashboard.route)
                    UserRole.ESTUDIANTE -> navController.navigate(Screen.AdminDashboard.route)
                    else -> navController.navigate(Screen.AdminDashboard.route)
                }
            })
        }
        composable(Screen.TeacherDashboard.route) {
            TeacherDashboard(navController)
        }
        composable(Screen.StudentDashboard.route) {
            StudentDashboard(navController)
        }
        composable(Screen.AdminDashboard.route) {
            AdminDashboard(navController)
        }
        composable(Screen.EvaluationForm.route) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
            EvaluationForm(roomId, navController)
        }
    }
}
