package crosenthal.com.libraryCalendar.scraper.domain

import java.time.LocalDate
import java.time.LocalTime

data class EventDateTime(
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime?,
)