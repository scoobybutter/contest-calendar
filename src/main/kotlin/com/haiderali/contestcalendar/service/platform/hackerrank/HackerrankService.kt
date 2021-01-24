package com.haiderali.contestcalendar.service.platform.hackerrank

import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.httpGet
import com.haiderali.contestcalendar.domain.Contest
import com.haiderali.contestcalendar.domain.Platform
import com.haiderali.contestcalendar.service.platform.CodingPlatformService
import com.haiderali.contestcalendar.util.DateTimeUtil
import org.springframework.stereotype.Component

@Component
class HackerrankService(
    private val klaxon: Klaxon
) : CodingPlatformService {

    override fun fetchContests() = getCollegeContests() + getGeneralContests()

    private fun getCollegeContests() = getContests("https://www.hackerrank.com/rest/contests/college")

    private fun getGeneralContests() = getContests("https://www.hackerrank.com/rest/contests/upcoming")

    private fun getContests(url: String): List<Contest> {
        val responseJson = url
            .httpGet()
            .header(Headers.USER_AGENT to "coding-calendar", Headers.ACCEPT to "application/json")
            .timeoutRead(10000)
            .responseString()
            .third.get()
        return klaxon.parse<HackerrankResponse>(responseJson)!!.models.map { getContestFromHackerrankModel(it) }
    }

    private fun getContestFromHackerrankModel(model: Model) = Contest(
        name = model.name,
        startTime = DateTimeUtil.getLocalDateTimeFromEpochSeconds(model.epochStarttime),
        endTime = DateTimeUtil.getLocalDateTimeFromEpochSeconds(model.epochEndtime),
        url = "https://www.hackerrank.com/${model.slug}",
        platform = Platform.HACKERRANK
    )
}

data class HackerrankResponse(
    val models: List<Model>,
    val total: Int
)

data class Model(
    val name: String,
    val slug: String,
    val epochStarttime: Long,
    val epochEndtime: Long
)
