package com.haiderali.contestcalendar.service.platform.codechef

import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.haiderali.contestcalendar.domain.Contest
import com.haiderali.contestcalendar.domain.Platform
import com.haiderali.contestcalendar.service.platform.CodingPlatformService
import com.haiderali.contestcalendar.util.DateTimeUtil
import org.springframework.stereotype.Component

@Component
class CodechefService(
    private val klaxon: Klaxon
) : CodingPlatformService {

    override fun fetchContests(): List<Contest> {
        val url = "https://api.codechef.com/oauth/token"
        val responseJson = url
            .httpPost()
            .header(Headers.CONTENT_TYPE to "application/json", Headers.ACCEPT to "application/json")
            .body(
                klaxon.toJsonString(
                    CodechefAccessTokenRequest(
                        grantType = "client_credentials",
                        clientId = System.getProperty("codechefClientId"),
                        clientSecret = System.getProperty("codechefClientSecret")
                    )
                )
            )
            .timeoutRead(10000)
            .responseString()
            .third.get()
        val accessToken = klaxon.parse<CodechefAccessTokenResponse>(responseJson)!!.result.data.accessToken
        return getCodechefContests(accessToken, "present") + getCodechefContests(accessToken, "future")
    }

    private fun getCodechefContests(accessToken: String, status: String): List<Contest> {
        val url = "https://api.codechef.com/contests?status=$status"
        val contestResponseJson = url
            .httpGet()
            .header("Authorization" to "Bearer $accessToken")
            .header(Headers.ACCEPT to "application/json")
            .timeoutRead(10000)
            .responseString()
            .third.get()
        return Klaxon().parse<CodechefContestResponse>(contestResponseJson)!!.result.data.content.contestList.map {
            getContestFromCodechefContest(it)
        }
    }

    private fun getContestFromCodechefContest(codechefContest: CodechefContest) = Contest(
        name = codechefContest.name,
        url = "https://codechef.com/${codechefContest.code}",
        platform = Platform.CODECHEF,
        startTime = DateTimeUtil.getLocalDateTimeFromStringWithZone(
            codechefContest.startDate + "+05:30",
            "yyyy-MM-dd HH:mm:ssz"
        ),
        endTime = DateTimeUtil.getLocalDateTimeFromStringWithZone(
            codechefContest.endDate + "+05:30",
            "yyyy-MM-dd HH:mm:ssz"
        )
    )
}

data class CodechefAccessTokenRequest(
    val grantType: String,
    val clientId: String,
    val clientSecret: String
)

data class CodechefAccessTokenResponse(
    val result: CodechefAccessTokenResponseResult,
)

data class CodechefAccessTokenResponseResult(
    val data: CodechefAccessTokenResponseData,
)

data class CodechefAccessTokenResponseData(
    val accessToken: String,
)

data class CodechefContestResponse(
    val result: CodechefContestResponseResult,
)

data class CodechefContestResponseResult(
    val data: CodechefContestResponseData,
)

data class CodechefContestResponseData(
    val content: CodechefContestResponseContent,
)

data class CodechefContestResponseContent(
    val contestList: List<CodechefContest>
)

data class CodechefContest(
    val code: String,
    val name: String,
    val startDate: String,
    val endDate: String
)
