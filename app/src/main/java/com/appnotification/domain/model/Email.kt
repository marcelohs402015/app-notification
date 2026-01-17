package com.appnotification.domain.model

data class Email(
    val id: String,
    val threadId: String,
    val from: String,
    val subject: String,
    val snippet: String,
    val body: String,
    val date: Long,
    val isRead: Boolean = false
)
