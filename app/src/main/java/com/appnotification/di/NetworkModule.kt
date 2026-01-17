package com.appnotification.di

import android.content.Context
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.gmail.GmailScopes
import com.google.api.services.calendar.CalendarScopes
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGoogleAccountCredential(@ApplicationContext context: Context): GoogleAccountCredential {
        val scopes = listOf(
            GmailScopes.GMAIL_READONLY,
            CalendarScopes.CALENDAR
        )
        return GoogleAccountCredential.usingOAuth2(context, scopes)
            .setBackOff(ExponentialBackOff())
    }

    @Provides
    fun provideGeminiApiKey(): String {
        // TODO: Replace with your actual Gemini API key from BuildConfig or local.properties
        return "YOUR_GEMINI_API_KEY"
    }
}
