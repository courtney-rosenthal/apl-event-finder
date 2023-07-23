package crosenthal.com.libraryCalendar.scraper.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime


internal class EventDateTimeTest {

    @Test
    fun dateTime() {
        val date = LocalDate.parse("2023-07-22")
        val startTime = LocalTime.parse("19:00")
        val endTime = LocalTime.parse("20:00")

        val a = EventDateTime.of(date, startTime, endTime)
        assertThat(a).isEqualTo(EventDateTime(
            start = Instant.parse("2023-07-23T00:00:00Z"),
            end = Instant.parse("2023-07-23T01:00:00Z"),
            localHourOfDay = 19,
            localDayOfWeek = "Sat",
        ))

        val b = EventDateTime.of(date, startTime, null)
        assertThat(b).isEqualTo(EventDateTime(
            start = Instant.parse("2023-07-23T00:00:00Z"),
            end = null,
            localHourOfDay = 19,
            localDayOfWeek = "Sat",
        ))
    }
}