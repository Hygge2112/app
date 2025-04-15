package com.example.growreminder.ui.screens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "AlarmReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Đã nhận báo thức!")

        val notificationId = intent.getIntExtra("NOTIFICATION_ID", 0)
        val task = intent.getStringExtra("TASK") ?: "Nhắc nhở công việc"
        val description = intent.getStringExtra("DESCRIPTION") ?: ""
        val datetime = intent.getStringExtra("DATETIME") ?: ""

        val notificationHelper = NotificationHelper(context)
        val message = if (description.isNotEmpty()) {
            "$description - $datetime"
        } else {
            "Đã đến thời gian thực hiện - $datetime"
        }

        notificationHelper.showNotification(notificationId, task, message)
    }
}