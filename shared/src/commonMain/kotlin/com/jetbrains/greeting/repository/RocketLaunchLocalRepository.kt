package com.jetbrains.greeting.repository

import RocketLaunch
import com.jetbrains.greeting.database.RocketLaunchEntity
import com.jetbrains.greeting.database.getRoomDatabase
import com.jetbrains.greeting.getDatabaseBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RocketLaunchLocalRepository {
    // リポジトリのスコープでコルーチンを起動するための CoroutineScope
    private val repositoryScope = CoroutineScope(Dispatchers.IO)
    private val rocketLaunchDao = getRoomDatabase(getDatabaseBuilder()).getRocketLaunchDao()
    private  val _rocketLaunchList = MutableStateFlow(emptyList<RocketLaunch>())
    val rocketLaunchList: Flow<List<RocketLaunch>> = _rocketLaunchList.asStateFlow()
    // DBからのFlowを直接公開 (StateFlowとして)
    val rocketLaunchListAsFlow: StateFlow<List<RocketLaunch>> =
        rocketLaunchDao.getAllEntities()
            .map { entities ->
                entities.map { entity ->
                    RocketLaunch(
                        flightNumber = entity.flightNumber,
                        missionName = entity.missionName,
                        launchDateUTC = entity.launchDateUTC,
                        launchSuccess = null // 必要に応じて設定
                    )
                }
            }
            .stateIn(
                scope = repositoryScope,
                started = SharingStarted.WhileSubscribed(5000), // 5秒間サブスクライバーがいなければ上流を停止
                initialValue = emptyList()
            )

//    init {
//        repositoryScope.launch {
////            rocketLaunchDao.observeAll().collect { entities ->
////                _rocketLaunchList.value = entities.map {
////                    RocketLaunch(
////                        flightNumber = it.flightNumber,
////                        missionName = it.missionName,
////                        launchDateUTC = it.launchDateUTC,
////                        launchSuccess = null
////                    )
////                }
////            }
//            _rocketLaunchList.value = rocketLaunchDao.getAll().map {
//                RocketLaunch(
//                    flightNumber = it.flightNumber,
//                    missionName = it.missionName,
//                    launchDateUTC = it.launchDateUTC,
//                    launchSuccess = null
//                )
//            }
//        }
//    }

    fun insertRocketLaunch(rocketLaunch: RocketLaunch) {
        val rocketLaunchEntity = RocketLaunchEntity(
            flightNumber = rocketLaunch.flightNumber,
            missionName = rocketLaunch.missionName,
            launchDateUTC = rocketLaunch.launchDateUTC,
        )
        repositoryScope.launch {
            rocketLaunchDao.insert(rocketLaunchEntity)
//            val currentRocketLaunchList = _rocketLaunchList.value.toMutableList()
//            currentRocketLaunchList.add(rocketLaunch)
//            _rocketLaunchList.value = currentRocketLaunchList
        }
    }
}
