package com.appnotification.domain.usecase

import com.appnotification.domain.model.Event
import com.appnotification.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEventsUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {
    operator fun invoke(): Flow<List<Event>> = eventRepository.getEvents()
}
