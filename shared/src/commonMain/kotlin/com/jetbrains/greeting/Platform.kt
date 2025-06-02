package com.jetbrains.greeting

import androidx.room.RoomDatabase
import com.jetbrains.greeting.database.AppDatabase

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
expect object AppContext
expect fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>
