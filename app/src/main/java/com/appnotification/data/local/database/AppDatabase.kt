package com.appnotification.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.appnotification.data.local.dao.EmailDao
import com.appnotification.data.local.dao.EventDao
import com.appnotification.data.local.entity.EmailEntity
import com.appnotification.data.local.entity.EventEntity

@Database(
    entities = [EmailEntity::class, EventEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun emailDao(): EmailDao
    abstract fun eventDao(): EventDao
}
