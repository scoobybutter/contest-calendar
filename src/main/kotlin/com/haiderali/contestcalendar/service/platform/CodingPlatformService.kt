package com.haiderali.contestcalendar.service.platform

import com.haiderali.contestcalendar.domain.Contest

interface CodingPlatformService {
    fun fetchContests(): List<Contest>
}
