package com.appnotification.domain.usecase

import com.appnotification.domain.model.Email
import com.appnotification.domain.repository.EmailRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEmailsUseCase @Inject constructor(
    private val emailRepository: EmailRepository
) {
    operator fun invoke(): Flow<List<Email>> = emailRepository.getEmails()
}
