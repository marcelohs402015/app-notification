package com.appnotification.data.mapper

import com.appnotification.data.local.entity.EmailEntity
import com.appnotification.domain.model.Email

object EmailMapper {
    fun toDomain(entity: EmailEntity): Email {
        return Email(
            id = entity.id,
            threadId = entity.threadId,
            from = entity.from,
            subject = entity.subject,
            snippet = entity.snippet,
            body = entity.body,
            date = entity.date,
            isRead = entity.isRead
        )
    }

    fun toEntity(domain: Email): EmailEntity {
        return EmailEntity(
            id = domain.id,
            threadId = domain.threadId,
            from = domain.from,
            subject = domain.subject,
            snippet = domain.snippet,
            body = domain.body,
            date = domain.date,
            isRead = domain.isRead
        )
    }

    fun toDomainList(entities: List<EmailEntity>): List<Email> {
        return entities.map { toDomain(it) }
    }

    fun toEntityList(domains: List<Email>): List<EmailEntity> {
        return domains.map { toEntity(it) }
    }
}
