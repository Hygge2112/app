package com.example.growreminder.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController



@Composable
fun StudyChoiceScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TitleSection(onBackClick = { navController.popBackStack() })
        Spacer(modifier = Modifier.height(50.dp))

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                ChoiceCard(text = "Ôn Bài", navController)
                ChoiceCard(text = "Đọc sách", navController)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                ChoiceCard(text = "Deadline", navController)
                ChoiceCard(text = "Làm Bài tập", navController)
            }
        }
    }
}

@Composable
fun TitleSection(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Lựa chọn Học tập, đọc sách",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4A148C)
        )
    }
}

@Composable
fun ChoiceCard(text: String, navController: NavController) {
    Box(
        modifier = Modifier
            .size(180.dp)
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .clickable { navController.navigate("destinationScreen") }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4A148C))
    }
}

@Composable
fun DestinationScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Trang đích", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4A148C))
    }
}
