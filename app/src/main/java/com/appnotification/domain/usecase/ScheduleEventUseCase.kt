package com.appnotification.domain.usecase

import com.appnotification.domain.model.Event
import com.appnotification.domain.model.Resource
import com.appnotification.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ScheduleEventUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {
    operator fun invoke(event: Event): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            eventRepository.scheduleEventInCalendar(event)
                .onSuccess {
                    eventRepository.saveEvent(event.copy(isScheduled = true))
                        .onFailure { throwable ->
                            emit(Resource.Error("Event scheduled but failed to save locally", throwable))
                            return@flow
                        }
                    emit(Resource.Success(Unit))
                }
                .onFailure { throwable ->
                    emit(Resource.Error("Failed to schedule event", throwable))
                }
        } catch (e: Exception) {
            emit(Resource.Error("Error scheduling event: ${e.message}", e))
        }
    }
}
