package com.appnotification.domain.repository

import com.appnotification.domain.model.Email
import kotlinx.coroutines.flow.Flow

interface EmailRepository {
    fun getEmails(): Flow<List<Email>>
    suspend fun fetchEmailsFromGmail(): Result<Unit>
    suspend fun getEmailById(emailId: String): Email?
    suspend fun markAsRead(emailId: String): Result<Unit>
    suspend fun syncEmails(): Result<Unit>
}
