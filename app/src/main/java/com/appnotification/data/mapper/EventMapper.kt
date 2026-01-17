package com.appnotification.data.mapper

import com.appnotification.data.local.entity.EventEntity
import com.appnotification.domain.model.Event
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object EventMapper {
    private val gson = Gson()

    fun toDomain(entity: EventEntity): Event {
        val attendeesType = object : TypeToken<List<String>>() {}.type
        val attendees = gson.fromJson<List<String>>(entity.attendees, attendeesType) ?: emptyList()

        return Event(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            startDateTime = entity.startDateTime,
            endDateTime = entity.endDateTime,
            location = entity.location,
            attendees = attendees,
            sourceEmailId = entity.sourceEmailId,
            isScheduled = entity.isScheduled
        )
    }

    fun toEntity(domain: Event): EventEntity {
        val attendeesJson = gson.toJson(domain.attendees)

        return EventEntity(
            id = domain.id ?: generateId(),
            title = domain.title,
            description = domain.description,
            startDateTime = domain.startDateTime,
            endDateTime = domain.endDateTime,
            location = domain.location,
            attendees = attendeesJson,
            sourceEmailId = domain.sourceEmailId,
            isScheduled = domain.isScheduled
        )
    }

    fun toDomainList(entities: List<EventEntity>): List<Event> {
        return entities.map { toDomain(it) }
    }

    private fun generateId(): String {
        return "event_${System.currentTimeMillis()}_${(0..9999).random()}"
    }
}
