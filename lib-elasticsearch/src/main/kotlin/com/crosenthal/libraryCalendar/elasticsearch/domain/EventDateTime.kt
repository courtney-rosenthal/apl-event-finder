package com.crosenthal.libraryCalendar.elasticsearch.domain

import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.TextStyle
import java.util.Locale

data class EventDateTime(
    @Field(type = FieldType.Date)
    val start: Instant,

    @Field(type = FieldType.Date)
    val end: Instant?,

    @Field(type = FieldType.Integer)
    val localHourOfDay: Int,

    @Field(type = FieldType.Keyword)
    val localDayOfWeek: String,
) {

    companion object {

        fun of(date: LocalDate, startTime: LocalTime, endTime: LocalTime?): EventDateTime {
            return EventDateTime(
                start = makeInstant(date, startTime),
                end = if (endTime == null) null else makeInstant(date, endTime),
                localHourOfDay = startTime.hour,
                localDayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)
            )
        }

        private fun makeInstant(date: LocalDate, time: LocalTime, zone: ZoneId = ZoneId.systemDefault()): Instant {
            return ZonedDateTime.of(date, time, zone).toInstant()
        }

    }

}
