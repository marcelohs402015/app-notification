package com.appnotification.data.remote.datasource

import com.appnotification.domain.model.Event
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event as CalendarEvent
import com.google.api.services.calendar.model.EventDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CalendarDataSource @Inject constructor() {
    private val transport = NetHttpTransport()
    private val jsonFactory = GsonFactory.getDefaultInstance()

    suspend fun scheduleEvent(
        credential: GoogleAccountCredential,
        event: Event
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val calendarService = Calendar.Builder(transport, jsonFactory, credential)
                .setApplicationName("App Notification")
                .build()

            val calendarEvent = CalendarEvent().apply {
                summary = event.title
                description = event.description
                location = event.location

                start = EventDateTime().apply {
                    dateTime = DateTime(event.startDateTime)
                    timeZone = "UTC"
                }

                end = EventDateTime().apply {
                    dateTime = DateTime(event.endDateTime)
                    timeZone = "UTC"
                }

                if (event.attendees.isNotEmpty()) {
                    attendees = event.attendees.map { email ->
                        com.google.api.services.calendar.model.EventAttendee().apply {
                            email = email
                        }
                    }
                }
            }

            val createdEvent = calendarService.events()
                .insert("primary", calendarEvent)
                .execute()

            Result.success(createdEvent.id ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
