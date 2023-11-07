package crosenthal.com.eventFinder.scraper.service

import com.crosenthal.eventFinder.elasticsearch.domain.EventDateTime
import com.crosenthal.eventFinder.elasticsearch.domain.RecommendedAge
import com.crosenthal.eventFinder.scraper.service.DateTimeParsers
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.assertj.core.api.ObjectAssert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeParseException

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

        assertThatExceptionOfType(DateTimeParseException::class.java).isThrownBy {
            parsers.parseLocalDate("oops")
        }
    }

    @Test
    fun parseLocalTime() {
        assertThat(parsers.parseLocalTime("7:00 PM")).isEqualTo(LocalTime.of(19, 0, 0))
        assertThat(parsers.parseLocalTime("13:00")).isEqualTo(LocalTime.of(13, 0, 0))
    }

    @Test
    fun parseRecommendedAge() {
        // helpers for this test
        fun eval(s: String) = assertThat(parsers.parseRecommendedAge(s))
        fun ObjectAssert<RecommendedAge>.verify(y1: Int?, y2: Int?) = isEqualTo(RecommendedAge(y1, y2))

        eval("All ages welcome").verify(null, null)
        eval("Teens 13-18 only").verify(13, 18)
        eval("Teens and adults welcome").verify(13, null)
        eval("Recommended for ages 3-5").verify(3, 5)
        eval("Recommended for ages 10 and up").verify(10, null)
        eval("Recommended for ages 5 and under").verify(null, 5)
        eval("Recommended for ages 18 months to 3 years").verify(2, 3) // 18 months rounds to 2 years

        assertThatIllegalArgumentException().isThrownBy {
            eval("oops")
        }
    }


    @Test
    fun parseEventDateTime() {
        // helpers for this test
        fun eval(s: String) = assertThat(parsers.parseEventDateTime(s))
        fun makeEventDateTime(date: String, startTime: String, endTime: String?) : EventDateTime {
            val a = if (endTime == null) null else LocalTime.parse(endTime)
            return EventDateTime.of(LocalDate.parse(date), LocalTime.parse(startTime), a)
        }

        eval("Wednesday, July 19, 2023 - 7:00 PM to 8:00 PM").isEqualTo(makeEventDateTime("2023-07-19", "19:00", "20:00"))
        eval("Wednesday, July 19, 2023 - 7:00 PM").isEqualTo(makeEventDateTime("2023-07-19", "19:00", null))

        assertThatIllegalArgumentException().isThrownBy {
            eval("oops")
        }
    }


}