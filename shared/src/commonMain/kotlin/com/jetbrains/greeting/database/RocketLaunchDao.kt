package com.jetbrains.greeting.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RocketLaunchDao {
    @Insert
    suspend fun insert(person: RocketLaunchEntity)

    @Query("SELECT * FROM RocketLaunchEntity")
    suspend fun getAll(): List<RocketLaunchEntity>

    @Query("SELECT * FROM RocketLaunchEntity")
    fun getAllEntities(): Flow<List<RocketLaunchEntity>> // Flowを返す
}