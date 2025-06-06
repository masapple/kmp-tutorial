package com.jetbrains.greeting

import RocketComponent
import daysPhrase
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration.Companion.seconds
class Greeting {
    private val platform = getPlatform()
    private val rocketComponent = RocketComponent()

    fun greet(): Flow<String> = flow {
        emit(if (Random.nextBoolean()) "Hi!" else "Hello!")
        delay(1.seconds)
        emit("Guess what this is! > ${platform.name.reversed()}")
        delay(1.seconds)
        emit(daysPhrase())
        emit(rocketComponent.launchPhrase())
    }
//    fun greet(): String {
//        val firstWord = if (Random.nextBoolean()) "Hi!" else "Hello!"
//        return "$firstWord Guess what this is! > ${platform.name.reversed()}!"
//    }

//    fun greet(): List<String> = buildList {
//        add(if (Random.nextBoolean()) "Hi!" else "Hello!")
//        add("Guess what this is! > ${platform.name.reversed()}!")
//        add(daysPhrase())
//    }
}