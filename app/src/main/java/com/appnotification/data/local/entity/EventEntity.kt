package com.appnotification.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val startDateTime: Long,
    val endDateTime: Long,
    val location: String?,
    val attendees: String, // JSON string for list
    val sourceEmailId: String,
    val isScheduled: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)
