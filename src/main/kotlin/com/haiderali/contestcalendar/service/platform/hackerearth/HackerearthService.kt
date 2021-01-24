package com.haiderali.contestcalendar.service.platform.hackerearth

import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.httpGet
import com.haiderali.contestcalendar.domain.Contest
import com.haiderali.contestcalendar.domain.Platform
import com.haiderali.contestcalendar.service.platform.CodingPlatformService
import com.haiderali.contestcalendar.util.DateTimeUtil.getLocalDateTimeFromStringWithZone
import org.springframework.stereotype.Component

@Component
class HackerearthService(
    private val klaxon: Klaxon
) : CodingPlatformService {

    override fun fetchContests(): List<Contest> {
        val url = "https://www.hackerearth.com/chrome-extension/events/"
        val responseJson = url
            .httpGet()
            .header(Headers.USER_AGENT to "coding-calendar", Headers.ACCEPT to "application/json")
            .timeoutRead(10000)
            .responseString()
            .third.get()
        return klaxon.parse<HackerearthResponse>(responseJson)!!.response.map { getContestFromHackerearthContest(it) }
    }

    private fun getContestFromHackerearthContest(hackerearthContest: HackerearthContest) = Contest(
        name = hackerearthContest.title,
        url = hackerearthContest.url,
        platform = Platform.HACKEREARTH,
        startTime = getLocalDateTimeFromStringWithZone(hackerearthContest.startUtcTz, "yyyy-MM-dd HH:mm:ssz"),
        endTime = getLocalDateTimeFromStringWithZone(hackerearthContest.endUtcTz, "yyyy-MM-dd HH:mm:ssz")
    )
}

data class HackerearthResponse(
    val response: List<HackerearthContest>,
)

data class HackerearthContest(
    val title: String,
    val url: String,
    val startUtcTz: String,
    val endUtcTz: String
)

