package com.appnotification.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.appnotification.domain.repository.EmailRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class EmailSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val emailRepository: EmailRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            emailRepository.syncEmails()
                .fold(
                    onSuccess = { androidx.work.ListenableWorker.Result.success() },
                    onFailure = { androidx.work.ListenableWorker.Result.retry() }
                )
        } catch (e: Exception) {
            androidx.work.ListenableWorker.Result.failure()
        }
    }
}
