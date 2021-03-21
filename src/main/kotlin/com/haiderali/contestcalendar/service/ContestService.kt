package com.haiderali.contestcalendar.service

import com.haiderali.contestcalendar.business.RefDataManager
import com.haiderali.contestcalendar.service.platform.CodingPlatformService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneOffset

@Component
class ContestService(
    private val refDataManager: RefDataManager,
    private val codingPlatformServices: List<CodingPlatformService>
) {

    fun getOpenContests(cached: Boolean) = getAllContests(cached).filter {
        LocalDateTime.now(ZoneOffset.UTC) >= it.startTime && LocalDateTime.now(ZoneOffset.UTC) <= it.endTime
    }

    fun getUpcomingContests(cached: Boolean) = getAllContests(cached).filter {
        it.startTime > LocalDateTime.now(ZoneOffset.UTC)
    }

    private fun getAllContests(cached: Boolean) = if (cached) {
        refDataManager.getAllContests()
    } else {
        val deferred = codingPlatformServices.map {
            GlobalScope.async {
                it.fetchContests()
            }
        }
        runBlocking {
            return@runBlocking deferred.flatMap { it.await() }
        }
    }
}
