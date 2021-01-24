package com.haiderali.contestcalendar.business

import com.haiderali.contestcalendar.domain.Contest
import com.haiderali.contestcalendar.service.platform.CodingPlatformService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct


@Component
class RefDataManager(
    private val codingPlatformServices: List<CodingPlatformService>
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    private lateinit var contests: List<Contest>

    @PostConstruct
    @Scheduled(fixedRate = 1000 * 60 * 15)
    fun init() {
        logger.info("Refreshing cache")
        val deferred = codingPlatformServices.map {
            GlobalScope.async {
                it.fetchContests()
            }
        }
        contests = runBlocking {
            return@runBlocking deferred.flatMap { it.await() }
        }
        logger.info("Cache refreshed")
    }

    fun getAllContests() = contests
}
