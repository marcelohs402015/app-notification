package com.appnotification.domain.model

data class Event(
    val id: String? = null,
    val title: String,
    val description: String,
    val startDateTime: Long,
    val endDateTime: Long,
    val location: String? = null,
    val attendees: List<String> = emptyList(),
    val sourceEmailId: String,
    val isScheduled: Boolean = false
)
