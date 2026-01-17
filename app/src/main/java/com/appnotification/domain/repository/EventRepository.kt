package com.appnotification.domain.repository

import com.appnotification.domain.model.Event
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun getEvents(): Flow<List<Event>>
    suspend fun extractEventsFromEmail(emailId: String, emailContent: String): Result<List<Event>>
    suspend fun saveEvent(event: Event): Result<Unit>
    suspend fun scheduleEventInCalendar(event: Event): Result<Unit>
    suspend fun getEventById(eventId: String): Event?
}
