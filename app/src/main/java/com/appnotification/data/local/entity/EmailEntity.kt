package com.appnotification.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emails")
data class EmailEntity(
    @PrimaryKey
    val id: String,
    val threadId: String,
    val from: String,
    val subject: String,
    val snippet: String,
    val body: String,
    val date: Long,
    val isRead: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)
