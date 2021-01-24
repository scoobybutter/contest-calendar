package com.haiderali.contestcalendar.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object DateTimeUtil {
    fun getLocalDateTimeFromStringWithZone(dateTimeString: String, format: String): LocalDateTime {
        return ZonedDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern(format))
            .withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }

    fun getLocalDateTimeFromEpochSeconds(epochSeconds: Long): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneOffset.UTC)
    }
}
