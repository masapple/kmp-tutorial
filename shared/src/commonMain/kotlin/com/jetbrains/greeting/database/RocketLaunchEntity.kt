package com.jetbrains.greeting.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RocketLaunchEntity (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val flightNumber: Int,
    val missionName: String,
    val launchDateUTC: String,
)
