package com.haiderali.contestcalendar.domain

import java.time.LocalDateTime

data class Contest(
    val name: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val url: String,
    val platform: Platform
)

enum class Platform {
    HACKERRANK,
    CODEFORCES,
    HACKEREARTH,
    CODECHEF,
    TOPCODER
}
