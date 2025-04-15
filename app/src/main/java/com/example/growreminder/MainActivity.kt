package com.example.growreminder

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.growreminder.ui.screens.*
import com.example.growreminder.ui.theme.GrowReminderTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            FirebaseApp.initializeApp(this)
            Log.d("Firebase", "Firebase initialized successfully")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val requestPermissionLauncher = registerForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted: Boolean ->
                    if (isGranted) {
                        Log.d("Permissions", "Notification permission granted")
                    } else {
                        Log.d("Permissions", "Notification permission denied")
                    }
                }

                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            // Test Firestore connection
            val db = FirebaseFirestore.getInstance()
            db.collection("test").document("test")
                .set(mapOf("test" to "value"))
                .addOnSuccessListener {
                    Log.d("Firebase", "Firestore connection successful")
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Firestore connection failed", e)
                }
        } catch (e: Exception) {
            Log.e("Firebase", "Firebase initialization failed", e)
        }

        setContent {
            GrowReminderTheme {
                val navController = rememberNavController()

                Scaffold { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "personalDevelopment",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        composable("personalDevelopment") {
                            PersonalDevelopmentScreen(navController)
                        }

                        composable("studyChoice") {
                            StudyChoiceScreen(navController)
                        }

                        composable("authScreen") {
                            AuthScreen()
                        }

                        composable("schedule") {
                            ScheduleScreen(navController)
                        }

                        composable("schedule_list") {
                            ScheduleListScreen(navController)
                        }

                    }
                }
            }
        }
    }
}
