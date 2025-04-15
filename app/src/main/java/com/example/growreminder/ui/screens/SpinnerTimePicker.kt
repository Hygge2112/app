package com.example.growreminder.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun SpinnerTimePicker(
    selectedDate: LocalDate,
    selectedHour: Int,
    selectedMinute: Int,
    onHourChanged: (Int) -> Unit,
    onMinuteChanged: (Int) -> Unit,
    currentTime: LocalTime = LocalTime.now()
) {
    val hours = (0..23).toList()
    val minutes = (0..59).toList()

    val hourState = rememberLazyListState(initialFirstVisibleItemIndex = selectedHour)
    val minuteState = rememberLazyListState(initialFirstVisibleItemIndex = selectedMinute)

    val coroutineScope = rememberCoroutineScope()

    val isToday = selectedDate == LocalDate.now()

    Row(
        modifier = Modifier
            .height(150.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        // Hour column
        LazyColumn(
            state = hourState,
            modifier = Modifier.width(80.dp)
        ) {
            items(hours.size) { index ->
                val hour = hours[index]
                val isPastHour = isToday && hour < currentTime.hour
                val isSelected = selectedHour == hour

                Text(
                    text = hour.toString().padStart(2, '0'),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .let {
                            if (!isPastHour) it.clickable {
                                onHourChanged(hour)
                                coroutineScope.launch {
                                    hourState.animateScrollToItem(index)
                                }
                            } else it
                        },
                    textAlign = TextAlign.Center,
                    color = if (isPastHour) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    else MaterialTheme.colorScheme.onSurface,
                    style = if (isSelected && !isPastHour)
                        MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    else
                        MaterialTheme.typography.bodyLarge
                )
            }
        }

        // Colon separator
        Text(
            ":",
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .align(Alignment.CenterVertically),
            style = MaterialTheme.typography.headlineMedium
        )

        // Minute column
        LazyColumn(
            state = minuteState,
            modifier = Modifier.width(80.dp)
        ) {
            items(minutes.size) { index ->
                val minute = minutes[index]
                val isPastMinute = isToday && selectedHour == currentTime.hour && minute < currentTime.minute
                val isPastHour = isToday && selectedHour < currentTime.hour
                val disabled = isPastHour || isPastMinute
                val isSelected = selectedMinute == minute

                Text(
                    text = minute.toString().padStart(2, '0'),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .let {
                            if (!disabled) it.clickable {
                                onMinuteChanged(minute)
                                coroutineScope.launch {
                                    minuteState.animateScrollToItem(index)
                                }
                            } else it
                        },
                    textAlign = TextAlign.Center,
                    color = if (disabled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    else MaterialTheme.colorScheme.onSurface,
                    style = if (isSelected && !disabled)
                        MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    else
                        MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
