package com.example.growreminder.ui.screens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class BootReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "BootReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "Thiết bị vừa khởi động lại, khôi phục các báo thức")
            restoreAlarms(context)
        }
    }

    private fun restoreAlarms(context: Context) {
        val dbRef = Firebase.database.reference.child("schedules")
        val alarmScheduler = AlarmScheduler(context)

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val scheduleItems = mutableListOf<ScheduleItem>()

                for (child in snapshot.children) {
                    val item = child.getValue(ScheduleItem::class.java)
                    val id = child.key ?: continue

                    if (item != null) {
                        scheduleItems.add(item.copy(id = id))
                    }
                }

                // Đặt lại tất cả báo thức
                alarmScheduler.rescheduleAlarmsAfterReboot(scheduleItems)
                Log.d(TAG, "Đã khôi phục ${scheduleItems.size} báo thức")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Lỗi khi khôi phục báo thức: ${error.message}")
            }
        })
    }
}