package com.appnotification.data.repository

import com.appnotification.data.local.dao.EmailDao
import com.appnotification.data.mapper.EmailMapper
import com.appnotification.data.remote.datasource.GmailDataSource
import com.appnotification.domain.model.Email
import com.appnotification.domain.repository.EmailRepository
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EmailRepositoryImpl @Inject constructor(
    private val emailDao: EmailDao,
    private val gmailDataSource: GmailDataSource,
    private val credential: GoogleAccountCredential
) : EmailRepository {

    override fun getEmails(): Flow<List<Email>> {
        return emailDao.getAllEmails().map { entities ->
            EmailMapper.toDomainList(entities)
        }
    }

    override suspend fun fetchEmailsFromGmail(): Result<Unit> {
        return gmailDataSource.fetchEmails(credential)
            .fold(
                onSuccess = { emails ->
                    val entities = EmailMapper.toEntityList(emails)
                    emailDao.insertEmails(entities)
                    Result.success(Unit)
                },
                onFailure = { exception ->
                    Result.failure(exception)
                }
            )
    }

    override suspend fun getEmailById(emailId: String): Email? {
        val entity = emailDao.getEmailById(emailId)
        return entity?.let { EmailMapper.toDomain(it) }
    }

    override suspend fun markAsRead(emailId: String): Result<Unit> {
        return try {
            emailDao.markAsRead(emailId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncEmails(): Result<Unit> {
        return fetchEmailsFromGmail()
    }
}
