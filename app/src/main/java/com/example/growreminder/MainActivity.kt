package com.example.growreminder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.example.growreminder.ui.screens.AuthScreen
import com.example.growreminder.ui.screens.PersonalDevelopmentScreen
import com.example.growreminder.ui.screens.StudyChoiceScreen
import com.example.growreminder.ui.theme.GrowReminderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GrowReminderTheme {
                val navController = rememberNavController()

                Scaffold { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "personalDevelopment",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding) // Thêm dòng này để sử dụng innerPadding
                    ) {
                        composable("personalDevelopment") { PersonalDevelopmentScreen(navController) }
                        composable("studyChoice") { StudyChoiceScreen(navController) }
                        composable("authScreen") { AuthScreen() }  // Nếu có màn hình đăng nhập
                    }
                }
            }
        }
    }
}