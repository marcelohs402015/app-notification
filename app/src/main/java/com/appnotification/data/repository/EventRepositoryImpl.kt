package com.appnotification.data.repository

import com.appnotification.data.local.dao.EventDao
import com.appnotification.data.mapper.EventMapper
import com.appnotification.data.remote.datasource.CalendarDataSource
import com.appnotification.data.remote.datasource.GeminiDataSource
import com.appnotification.domain.model.Event
import com.appnotification.domain.repository.EventRepository
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val eventDao: EventDao,
    private val geminiDataSource: GeminiDataSource,
    private val calendarDataSource: CalendarDataSource,
    private val credential: GoogleAccountCredential,
    private val geminiApiKey: String
) : EventRepository {

    override fun getEvents(): Flow<List<Event>> {
        return eventDao.getAllEvents().map { entities ->
            EventMapper.toDomainList(entities)
        }
    }

    override suspend fun extractEventsFromEmail(
        emailId: String,
        emailContent: String
    ): Result<List<Event>> {
        return geminiDataSource.extractEventsFromEmail(geminiApiKey, emailContent)
            .fold(
                onSuccess = { events ->
                    val eventsWithEmailId = events.map { it.copy(sourceEmailId = emailId) }
                    eventsWithEmailId.forEach { event ->
                        val entity = EventMapper.toEntity(event)
                        eventDao.insertEvent(entity)
                    }
                    Result.success(eventsWithEmailId)
                },
                onFailure = { exception ->
                    Result.failure(exception)
                }
            )
    }

    override suspend fun saveEvent(event: Event): Result<Unit> {
        return try {
            val entity = EventMapper.toEntity(event)
            eventDao.insertEvent(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun scheduleEventInCalendar(event: Event): Result<Unit> {
        return calendarDataSource.scheduleEvent(credential, event)
            .fold(
                onSuccess = { calendarEventId ->
                    val updatedEvent = event.copy(id = calendarEventId)
                    saveEvent(updatedEvent).getOrElse {
                        return Result.failure(it)
                    }
                    Result.success(Unit)
                },
                onFailure = { exception ->
                    Result.failure(exception)
                }
            )
    }

    override suspend fun getEventById(eventId: String): Event? {
        val entity = eventDao.getEventById(eventId)
        return entity?.let { EventMapper.toDomain(it) }
    }
}
