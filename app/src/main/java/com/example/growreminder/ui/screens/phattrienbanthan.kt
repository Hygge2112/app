package com.example.growreminder.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun PersonalDevelopmentScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Nút trở về
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF4A148C))
            }
            Text(
                text = "Phát triển bản thân",
                fontSize = 20.sp,
                color = Color(0xFF4A148C),
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Các mục chọn
        ChoiceCard("Học Tập, đọc sách") { navController.navigate("studyChoice") }
        ChoiceCard("Sức khỏe") { /* Điều hướng sau này */ }
        ChoiceCard("Kỹ năng mới") { /* Điều hướng sau này */ }
    }
}

@Composable
fun ChoiceCard(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color(0xFFEDE7F6), shape = RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontSize = 18.sp, color = Color(0xFF4A148C))
    }
}