package com.appnotification.domain.usecase

import com.appnotification.domain.model.Resource
import com.appnotification.domain.repository.EmailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FetchEmailsUseCase @Inject constructor(
    private val emailRepository: EmailRepository
) {
    operator fun invoke(): Flow<Resource<List<com.appnotification.domain.model.Email>>> = flow {
        emit(Resource.Loading)
        try {
            emailRepository.fetchEmailsFromGmail()
                .onSuccess {
                    emailRepository.getEmails().collect { emails ->
                        emit(Resource.Success(emails))
                    }
                }
                .onFailure { throwable ->
                    emit(Resource.Error("Failed to fetch emails", throwable))
                }
        } catch (e: Exception) {
            emit(Resource.Error("Error fetching emails: ${e.message}", e))
        }
    }
}
