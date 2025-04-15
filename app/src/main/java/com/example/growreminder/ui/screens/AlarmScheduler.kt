package com.example.growreminder.ui.screens

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AlarmScheduler(private val context: Context) {

    companion object {
        private const val TAG = "AlarmScheduler"
    }

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleAlarm(id: String, task: String, description: String, date: String, time: String) {
        try {
            val alarmTime = getTimeInMillis(date, time)
            val currentTime = System.currentTimeMillis()

            // Chỉ đặt báo thức cho tương lai
            if (alarmTime > currentTime) {
                val intent = Intent(context, AlarmReceiver::class.java).apply {
                    putExtra("NOTIFICATION_ID", id.hashCode())
                    putExtra("TASK", task)
                    putExtra("DESCRIPTION", description)
                    putExtra("DATETIME", "$date $time")
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    id.hashCode(),
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        alarmTime,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        alarmTime,
                        pendingIntent
                    )
                }

                val minutesUntilAlarm = TimeUnit.MILLISECONDS.toMinutes(alarmTime - currentTime)
                Log.d(TAG, "Báo thức đã được đặt cho: $task - $date $time " +
                        "(còn $minutesUntilAlarm phút)")
            } else {
                Log.d(TAG, "Bỏ qua báo thức đã quá hạn: $date $time")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi khi đặt báo thức: ${e.message}")
        }
    }

    fun cancelAlarm(id: String) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )

        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
            Log.d(TAG, "Đã hủy báo thức ID: $id")
        }
    }

    private fun getTimeInMillis(date: String, time: String): Long {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = dateFormat.parse("$date $time") ?: Date()
        return calendar.timeInMillis
    }

    fun rescheduleAlarmsAfterReboot(scheduleItems: List<ScheduleItem>) {
        for (item in scheduleItems) {
            scheduleAlarm(item.id, item.task, item.description, item.date, item.time)
        }
    }
}

// Copy lại data class từ ScheduleListScreen
//data class ScheduleItem(
//    val id: String = "",
//    val task: String = "",
//    val description: String = "",
//    val date: String = "",
//    val time: String = "",
//    val timestamp: Long = 0L
//)