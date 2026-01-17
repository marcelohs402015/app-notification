package com.appnotification.domain.usecase

import com.appnotification.domain.model.Event
import com.appnotification.domain.model.Resource
import com.appnotification.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ExtractEventsFromEmailUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {
    operator fun invoke(emailId: String, emailContent: String): Flow<Resource<List<Event>>> = flow {
        emit(Resource.Loading)
        try {
            eventRepository.extractEventsFromEmail(emailId, emailContent)
                .onSuccess { events ->
                    emit(Resource.Success(events))
                }
                .onFailure { throwable ->
                    emit(Resource.Error("Failed to extract events", throwable))
                }
        } catch (e: Exception) {
            emit(Resource.Error("Error extracting events: ${e.message}", e))
        }
    }
}
