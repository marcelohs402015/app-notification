package com.appnotification.data.remote.datasource

import com.appnotification.domain.model.Email
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.ListMessagesResponse
import com.google.api.services.gmail.model.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Base64
import javax.inject.Inject

class GmailDataSource @Inject constructor() {
    private val transport = NetHttpTransport()
    private val jsonFactory = GsonFactory.getDefaultInstance()

    suspend fun fetchEmails(credential: GoogleAccountCredential): Result<List<Email>> = withContext(Dispatchers.IO) {
        try {
            val gmailService = Gmail.Builder(transport, jsonFactory, credential)
                .setApplicationName("App Notification")
                .build()

            val userId = "me"
            val listResponse: ListMessagesResponse = gmailService.users().messages()
                .list(userId)
                .setMaxResults(50L)
                .execute()

            val messages = listResponse.messages ?: emptyList()
            val emails = mutableListOf<Email>()

            for (message in messages) {
                val fullMessage: Message = gmailService.users().messages()
                    .get(userId, message.id)
                    .setFormat("full")
                    .execute()

                val email = parseMessage(fullMessage)
                emails.add(email)
            }

            Result.success(emails)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEmailById(credential: GoogleAccountCredential, emailId: String): Result<Email> = withContext(Dispatchers.IO) {
        try {
            val gmailService = Gmail.Builder(transport, jsonFactory, credential)
                .setApplicationName("App Notification")
                .build()

            val fullMessage: Message = gmailService.users().messages()
                .get("me", emailId)
                .setFormat("full")
                .execute()

            Result.success(parseMessage(fullMessage))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseMessage(message: Message): Email {
        val headers = message.payload?.headers ?: emptyList()
        val from = headers.firstOrNull { it.name.equals("From", ignoreCase = true) }?.value ?: ""
        val subject = headers.firstOrNull { it.name.equals("Subject", ignoreCase = true) }?.value ?: ""
        val dateHeader = headers.firstOrNull { it.name.equals("Date", ignoreCase = true) }?.value ?: ""
        val date = parseDate(dateHeader)

        val body = extractBody(message.payload)
        val snippet = message.snippet ?: ""

        return Email(
            id = message.id ?: "",
            threadId = message.threadId ?: "",
            from = from,
            subject = subject,
            snippet = snippet,
            body = body,
            date = date,
            isRead = message.labelIds?.contains("UNREAD") != true
        )
    }

    private fun extractBody(payload: Message.Payload?): String {
        if (payload == null) return ""

        if (payload.body?.data != null) {
            return decodeBase64(payload.body.data)
        }

        val parts = payload.parts ?: emptyList()
        for (part in parts) {
            if (part.mimeType == "text/plain" && part.body?.data != null) {
                return decodeBase64(part.body.data)
            }
            if (part.mimeType == "text/html" && part.body?.data != null) {
                return decodeBase64(part.body.data)
            }
        }

        return ""
    }

    private fun decodeBase64(data: String): String {
        return try {
            val decodedBytes = Base64.getUrlDecoder().decode(data)
            String(decodedBytes)
        } catch (e: Exception) {
            ""
        }
    }

    private fun parseDate(dateString: String): Long {
        return try {
            java.text.SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", java.util.Locale.ENGLISH)
                .parse(dateString)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }
}
