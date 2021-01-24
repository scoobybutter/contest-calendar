package com.haiderali.contestcalendar.service.platform.codeforces

import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.httpGet
import com.haiderali.contestcalendar.domain.Contest
import com.haiderali.contestcalendar.domain.Platform
import com.haiderali.contestcalendar.service.platform.CodingPlatformService
import com.haiderali.contestcalendar.util.DateTimeUtil.getLocalDateTimeFromEpochSeconds
import org.springframework.stereotype.Component

@Component
class CodeforcesService : CodingPlatformService {

    override fun fetchContests(): List<Contest> {
        val url = "http://codeforces.com/api/contest.list"
        val responseJson = url
            .httpGet()
            .header(Headers.USER_AGENT to "coding-calendar", Headers.ACCEPT to "application/json")
            .timeoutRead(10000)
            .responseString()
            .third.get()
        return Klaxon().parse<CodeforcesResponse>(responseJson)!!.result.map { getContestFromCodeforcesContest(it) }
    }

    private fun getContestFromCodeforcesContest(codeforcesContest: CodeforcesContest) = Contest(
        name = codeforcesContest.name,
        url = codeforcesContest.websiteUrl ?: "http://codeforces.com/contests/${codeforcesContest.id}",
        platform = Platform.CODEFORCES,
        startTime = getLocalDateTimeFromEpochSeconds(codeforcesContest.startTimeSeconds),
        endTime = getLocalDateTimeFromEpochSeconds(codeforcesContest.startTimeSeconds + codeforcesContest.durationSeconds)
    )
}

data class CodeforcesResponse(
    val result: List<CodeforcesContest>,
)

data class CodeforcesContest(
    val id: Long,
    val name: String,
    val startTimeSeconds: Long,
    val durationSeconds: Long,
    val websiteUrl: String? = null
)
