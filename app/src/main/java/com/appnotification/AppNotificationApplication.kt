package com.appnotification

import android.app.Application
import com.appnotification.util.WorkManagerScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AppNotificationApplication : Application() {

    @Inject
    lateinit var workManagerScheduler: WorkManagerScheduler

    override fun onCreate() {
        super.onCreate()
        // WorkManager will be initialized after Hilt injection
        // Schedule work in MainActivity after user authentication
    }
}
