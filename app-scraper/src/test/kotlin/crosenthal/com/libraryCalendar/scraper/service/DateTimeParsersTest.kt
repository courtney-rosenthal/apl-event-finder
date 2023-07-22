package crosenthal.com.libraryCalendar.scraper.service

import crosenthal.com.libraryCalendar.scraper.domain.RecommendedAge
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.ObjectAssert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalTime
import java.time.Period

internal class DateTimeParsersTest {
    private lateinit var parsers: DateTimeParsers

    @BeforeEach
    fun setup() {
        parsers = DateTimeParsers()
    }

    @Test
    fun parseLocalDate() {
        assertThat(parsers.parseLocalDate("Wednesday, July 19, 2023")).isEqualTo(LocalDate.of(2023, 7, 19))
        assertThat(parsers.parseLocalDate("WEDNESDAY, JULY 19, 2023")).isEqualTo(LocalDate.of(2023, 7, 19))
        assertThat(parsers.parseLocalDate("Lunes, Julio 24, 2023")).isEqualTo(LocalDate.of(2023, 7, 24))
    }

    @Test
    fun parseLocalTime() {
        assertThat(parsers.parseLocalTime("7:00 PM")).isEqualTo(LocalTime.of(19, 0, 0))
    }

    @Test
    fun parseRecommendedAge() {

        // helpers for this test
        fun eval(s: String) = assertThat(parsers.parseRecommendedAge(s))
        fun period(x: Any?): Period? {
            return when (x) {
                null -> null
                is Int -> Period.ofYears(x)
                is Period ->  x
                else -> throw IllegalArgumentException("cannot grok: $x")
            }
        }
        fun ObjectAssert<RecommendedAge>.verify(y1: Any?, y2: Any?) = isEqualTo(RecommendedAge(period(y1), period(y2)))

        eval("All ages welcome").verify(null, null)
        eval("Teens 13-18 only").verify(13, 18)
        eval("Teens and adults welcome").verify(13, null)
        eval("Recommended for ages 3-5").verify(3, 5)
        eval("Recommended for ages 10 and up").verify(10, null)
        eval("Recommended for ages 5 and under").verify(null, 5)
        eval("Recommended for ages 18 months to 3 years").verify(Period.ofMonths(18), 3)
    }


}