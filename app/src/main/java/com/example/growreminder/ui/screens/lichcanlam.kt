package com.example.growreminder.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.ktx.database
import com.example.growreminder.ui.screens.AlarmScheduler

data class ScheduleItem(
    val id: String = "", // 🆔 key để xóa
    val task: String = "",
    val description: String = "",
    val date: String = "",
    val time: String = "",
    val timestamp: Long = 0L
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleListScreen(navController: NavController) {
    val context = LocalContext.current
    val scheduleList = remember { mutableStateListOf<ScheduleItem>() }
    val alarmScheduler = remember { AlarmScheduler(context) }

    // Trạng thái cho dialog chỉnh sửa
    val showEditDialog = remember { mutableStateOf(false) }
    val selectedItem = remember { mutableStateOf<ScheduleItem?>(null) }
    val editedTask = remember { mutableStateOf("") }
    val editedDescription = remember { mutableStateOf("") }

    // 👂 Lắng nghe realtime từ Firebase
    DisposableEffect(Unit) {
        val dbRef = Firebase.database.reference.child("schedules")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempList = mutableListOf<ScheduleItem>()
                for (child in snapshot.children) {
                    val item = child.getValue(ScheduleItem::class.java)
                    val id = child.key ?: continue
                    if (item != null) {
                        tempList.add(item.copy(id = id)) // 🆔 giữ lại id để xóa
                    }
                }
                scheduleList.clear()
                scheduleList.addAll(tempList.sortedByDescending { it.timestamp }) // 🕒 sắp xếp mới nhất
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show()
            }
        }

        dbRef.addValueEventListener(listener)

        onDispose {
            dbRef.removeEventListener(listener)
        }
    }

    // Dialog chỉnh sửa
    if (showEditDialog.value && selectedItem.value != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog.value = false },
            title = { Text("Chỉnh sửa lịch") },
            text = {
                Column {
                    Text("Thời gian: ${selectedItem.value?.time}; ${selectedItem.value?.date}",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp))

                    OutlinedTextField(
                        value = editedTask.value,
                        onValueChange = { editedTask.value = it },
                        label = { Text("Việc cần làm") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )

                    OutlinedTextField(
                        value = editedDescription.value,
                        onValueChange = { editedDescription.value = it },
                        label = { Text("Mô tả") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedItem.value?.let { item ->
                            // Cập nhật dữ liệu trên Firebase
                            val updates = mapOf(
                                "task" to editedTask.value,
                                "description" to editedDescription.value
                            )

                            Firebase.database.reference
                                .child("schedules")
                                .child(item.id)
                                .updateChildren(updates)
                                .addOnSuccessListener {
                                    // Cập nhật lại báo thức
                                    alarmScheduler.cancelAlarm(item.id)
                                    alarmScheduler.scheduleAlarm(
                                        id = item.id,
                                        task = editedTask.value,
                                        description = editedDescription.value,
                                        date = item.date,
                                        time = item.time
                                    )

                                    Toast.makeText(context, "Đã cập nhật lịch", Toast.LENGTH_SHORT).show()
                                    showEditDialog.value = false
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                ) {
                    Text("Lưu thay đổi")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog.value = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch cần làm") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(scheduleList) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EDFF)),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("${item.time}; ${item.date}", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Việc cần làm: ${item.task}")
                        Text("Mô tả: ${item.description}")
                        Spacer(modifier = Modifier.height(12.dp))

                        // Thêm Row để chứa 2 nút cạnh nhau
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Nút "Chỉnh sửa"
                            Button(
                                onClick = {
                                    selectedItem.value = item
                                    editedTask.value = item.task
                                    editedDescription.value = item.description
                                    showEditDialog.value = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Chỉnh sửa", color = Color.White)
                            }

                            // Nút "Xóa lịch"
                            Button(
                                onClick = {
                                    Firebase.database.reference
                                        .child("schedules")
                                        .child(item.id)
                                        .removeValue()
                                        .addOnSuccessListener {
                                            // Hủy báo thức
                                            alarmScheduler.cancelAlarm(item.id)
                                            Toast.makeText(context, "Đã xóa lịch và hủy nhắc nhở", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, "Xóa thất bại", Toast.LENGTH_SHORT).show()
                                        }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Xóa lịch", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}