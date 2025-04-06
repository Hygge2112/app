package com.example.growreminder.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import androidx.navigation.NavController

@Composable
fun ScheduleScreen(navController: NavController) {
    val context = LocalContext.current
    val currentDate = remember { LocalDate.now() }

    val formatter = remember { DateTimeFormatter.ofPattern("hh:mm a") }
    var currentTime by remember { mutableStateOf(LocalTime.now()) }
    val nowFormatted = remember(currentTime) { currentTime.format(formatter) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = LocalTime.now()
            delay(1000)
        }
    }

    val selectedDate = remember { mutableStateOf(currentDate) }
    val selectedTime = remember { mutableStateOf("") }
    val selectedExactTime = remember { mutableStateOf<LocalTime?>(null) }

    val lazyListState = rememberLazyListState()

    val days = remember {
        List(365 * 5) { offset -> currentDate.plusDays(offset.toLong()) }
    }

    LaunchedEffect(selectedDate.value) {
        val index = days.indexOfFirst { it == selectedDate.value }
        if (index >= 0) {
            lazyListState.animateScrollToItem(index)
        }
    }

    // 👇 Toggle AM/PM
    val isAM = remember { mutableStateOf(true) }

    val amTimeSlots = listOf(
        "01:00 AM", "02:00 AM", "03:00 AM", "04:00 AM",
        "05:00 AM", "06:00 AM", "07:00 AM", "08:00 AM",
        "09:00 AM", "10:00 AM", "11:00 AM", "12:00 AM"
    )

    val pmTimeSlots = listOf(
        "01:00 PM", "02:00 PM", "03:00 PM", "04:00 PM",
        "05:00 PM", "06:00 PM", "07:00 PM", "08:00 PM",
        "09:00 PM", "10:00 PM", "11:00 PM", "12:00 PM"
    )

    val displayedSlots = if (isAM.value) amTimeSlots else pmTimeSlots

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 🗓 Ngày giờ hiện tại
        Text(
            text = "Ngày đã chọn: ${selectedDate.value.dayOfMonth}/${selectedDate.value.monthValue}/${selectedDate.value.year} - $nowFormatted",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // 🔙 Nút Back + Tiêu đề
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Lịch đọc sách", style = MaterialTheme.typography.titleLarge)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 📅 LazyRow cho ngày dài hạn
        LazyRow(state = lazyListState) {
            items(days) { date ->
                val isSelected = selectedDate.value == date
                val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(0.2f)
                val textColor = if (isSelected) Color.White else Color.Black

                Column(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(backgroundColor)
                        .clickable { selectedDate.value = date }
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("${date.dayOfMonth}", color = textColor, style = MaterialTheme.typography.bodyLarge)
                    Text(date.dayOfWeek.name.take(3), color = textColor, style = MaterialTheme.typography.labelSmall)
                    Text("${date.monthValue}/${date.year}", color = textColor, style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ✅ Nút chọn ngày cụ thể
        Button(
            onClick = {
                val calendar = Calendar.getInstance()
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        selectedDate.value = LocalDate.of(year, month + 1, dayOfMonth)
                    },
                    selectedDate.value.year,
                    selectedDate.value.monthValue - 1,
                    selectedDate.value.dayOfMonth
                ).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Chọn ngày cụ thể")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ⏰ "Available Time" + AM/PM Toggle cùng hàng
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Available Time",
                style = MaterialTheme.typography.titleMedium
            )
            Row {
                Button(
                    onClick = { isAM.value = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAM.value) MaterialTheme.colorScheme.primary else Color.LightGray
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Text("AM", color = if (isAM.value) Color.White else Color.Black)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { isAM.value = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isAM.value) MaterialTheme.colorScheme.primary else Color.LightGray
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Text("PM", color = if (!isAM.value) Color.White else Color.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ⏰ Các khung giờ
        FlowRow(
            mainAxisSpacing = 12.dp,
            crossAxisSpacing = 12.dp
        ) {
            displayedSlots.forEach { time ->
                val isNow = time == nowFormatted
                val isSelected = selectedTime.value == time
                val background = when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    isNow -> Color.Green.copy(alpha = 0.7f)
                    else -> Color.LightGray.copy(0.2f)
                }

                val textColor = when {
                    isSelected || isNow -> Color.White
                    else -> Color.Black
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(background)
                        .clickable { selectedTime.value = time }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                ) {
                    Text(text = time, color = textColor)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 🆕 Nút chọn giờ cụ thể
        Button(
            onClick = {
                TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        val pickedTime = LocalTime.of(hour, minute)
                        selectedExactTime.value = pickedTime
                        selectedTime.value = pickedTime.format(formatter)
                    },
                    currentTime.hour,
                    currentTime.minute,
                    false // AM/PM format
                ).show()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Chọn giờ cụ thể", color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 📚 Thông tin công việc
        Row {
            Text("Việc cần làm :", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Đọc sách")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            Text("Mô tả :", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sách kỹ năng bán hàng")
        }

        Spacer(modifier = Modifier.weight(1f))

        // 🔘 Nút Thêm lịch
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            onClick = {
                // TODO: xử lý thêm lịch
            }
        ) {
            Text("Thêm lịch")
        }
    }
}
