package com.appnotification.di

import android.content.Context
import androidx.room.Room
import com.appnotification.data.local.dao.EmailDao
import com.appnotification.data.local.dao.EventDao
import com.appnotification.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_notification_database"
        ).build()
    }

    @Provides
    fun provideEmailDao(database: AppDatabase): EmailDao {
        return database.emailDao()
    }

    @Provides
    fun provideEventDao(database: AppDatabase): EventDao {
        return database.eventDao()
    }
}
