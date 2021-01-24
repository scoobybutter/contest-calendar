package com.haiderali.contestcalendar.service.platform.topcoder

import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.httpGet
import com.haiderali.contestcalendar.domain.Contest
import com.haiderali.contestcalendar.domain.Platform
import com.haiderali.contestcalendar.service.platform.CodingPlatformService
import com.haiderali.contestcalendar.util.DateTimeUtil
import org.springframework.stereotype.Component

@Component
class TopcoderService : CodingPlatformService {

    override fun fetchContests(): List<Contest> {
        val url =
            "https://clients6.google.com/calendar/v3/calendars/" +
                    "appirio.com_bhga3musitat85mhdrng9035jg@group.calendar.google.com/events" +
                    "?calendarId=appirio.com_bhga3musitat85mhdrng9035jg%40group.calendar.google.com" +
                    "&timeMin=2020-01-01T00%3A00%3A00-04%3A00" +
                    "&key=AIzaSyBNlYH01_9Hc5S1J9vuFmu2nUqBZJNAXxs"
        val responseJson = url
            .httpGet()
            .header(Headers.USER_AGENT to "coding-calendar", Headers.ACCEPT to "application/json")
            .timeoutRead(10000)
            .responseString()
            .third.get()
        return Klaxon().parse<TopcoderResponse>(responseJson)!!
            .items
            .filter { it.summary != null && it.start != null && it.end != null && it.start.dateTime != null && it.end.dateTime != null }
            .map { getContestFromTopcoderContest(it) }
    }

    private fun getContestFromTopcoderContest(topcoderContest: TopcoderContest) = Contest(
        name = topcoderContest.summary!!,
        url = "http://topcoder.com/challenges",
        platform = Platform.TOPCODER,
        startTime = DateTimeUtil.getLocalDateTimeFromStringWithZone(
            topcoderContest.start!!.dateTime!!, "yyyy-MM-dd'T'HH:mm:ssz"
        ),
        endTime = DateTimeUtil.getLocalDateTimeFromStringWithZone(
            topcoderContest.end!!.dateTime!!, "yyyy-MM-dd'T'HH:mm:ssz"
        )
    )
}

data class TopcoderResponse(
    val items: List<TopcoderContest>
)

data class TopcoderContest(
    val summary: String? = null,
    val start: TopcoderDateTime? = null,
    val end: TopcoderDateTime? = null
)

data class TopcoderDateTime(
    val dateTime: String? = null
)
