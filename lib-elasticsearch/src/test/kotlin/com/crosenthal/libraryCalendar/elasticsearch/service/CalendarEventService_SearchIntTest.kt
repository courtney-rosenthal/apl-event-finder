package com.crosenthal.libraryCalendar.elasticsearch.service

import com.crosenthal.libraryCalendar.elasticsearch.ElasticsearchConfig
import com.crosenthal.libraryCalendar.elasticsearch.ElasticsearchProperties
import com.crosenthal.libraryCalendar.elasticsearch.domain.CalendarEvent
import com.crosenthal.libraryCalendar.elasticsearch.domain.EventDateTime
import com.crosenthal.libraryCalendar.elasticsearch.domain.RecommendedAge
import com.crosenthal.libraryCalendar.elasticsearch.misc.SearchConditions.AttendeeAge
import com.crosenthal.libraryCalendar.elasticsearch.misc.SearchConditions.Day
import com.crosenthal.libraryCalendar.elasticsearch.misc.SearchConditions.Time
import com.crosenthal.libraryCalendar.elasticsearch.repository.CalendarEventRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID


@SpringBootTest(classes = [ElasticsearchProperties::class, ElasticsearchConfig::class, CalendarEventService::class])
@EnableConfigurationProperties(ElasticsearchProperties::class)
internal class CalendarEventService_SearchIntTest {

    @Autowired @Qualifier("calendarEventRepository")
    lateinit var repository: CalendarEventRepository

    @Autowired @Qualifier("calendarEventService")
    lateinit var service: CalendarEventService

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    private fun makeEvent(
        url: String,
        content: String =  UUID.randomUUID().toString(),
        time: EventDateTime? = null,
        recommendedAge: RecommendedAge? = null
    ): CalendarEvent {
        return repository.save(CalendarEvent(
            url = url,
            content = content,
            time = time,
            recommendedAge = recommendedAge,
        ))
    }

    private fun makeTime(date: String = "2023-01-01", startTime: String = "12:00", endTime: String = "13:00"): EventDateTime {
        return EventDateTime.of(LocalDate.parse(date), LocalTime.parse(startTime), LocalTime.parse(endTime))
    }

    private fun makeAge(minYears: Int?, maxYears: Int?) : RecommendedAge {
        return RecommendedAge(minYears, maxYears)
    }

    @Test
    fun `search with no arguments returns all events`() {
        val a = makeEvent("a")
        val b = makeEvent("b")
        val result = service.search()
        assertThat(result).containsExactlyInAnyOrder(a, b)
    }

    @Test
    fun `search by localDayOfWeek`() {
        val sat = makeEvent("sat", time = makeTime(date = "2023-07-08"))
        val sun = makeEvent("sun", time = makeTime(date = "2023-07-09"))
        val mon = makeEvent("mon", time = makeTime(date = "2023-07-10"))
        val tue = makeEvent("tue", time = makeTime(date = "2023-07-11"))

        assertThat(service.search(days = setOf(Day.SAT))).containsExactlyInAnyOrder(sat)
        assertThat(service.search(days = setOf(Day.MON))).containsExactlyInAnyOrder(mon)
        assertThat(service.search(days = setOf(Day.SAT, Day.MON))).containsExactlyInAnyOrder(sat, mon)
        assertThat(service.search(days = setOf(Day.WEEKDAY))).containsExactlyInAnyOrder(mon, tue)
        assertThat(service.search(days = setOf(Day.WEEKEND))).containsExactlyInAnyOrder(sat, sun)
    }

    @Test
    fun `search by localHourOfDay`() {
        val morning = makeEvent("morning", time = makeTime(startTime = "10:00"))
        val afternoon = makeEvent("afternoon", time = makeTime(startTime = "13:00"))
        val evening = makeEvent("evening", time = makeTime(startTime = "19:00"))

        assertThat(service.search(times = setOf(Time.MORNING))).containsExactlyInAnyOrder(morning)
        assertThat(service.search(times = setOf(Time.EVENING))).containsExactlyInAnyOrder(evening)
        assertThat(service.search(times = setOf(Time.EVENING, Time.MORNING))).containsExactlyInAnyOrder(morning, evening)
    }

    @Test
    @Disabled
    fun `search by branches`() {
        TODO()
    }

    @Test
    fun `search by attendeeAge`() {
        val age_0_5 = makeEvent("age_0_5", recommendedAge = makeAge(null, 5))
        val age_0_7 = makeEvent("age_0_7", recommendedAge = makeAge(null, 7))
        val age_5_10 = makeEvent("age_5_10", recommendedAge = makeAge(5, 10))
        val age_7_up = makeEvent("age_7_up", recommendedAge = makeAge(7, null))
        val age_13_18 = makeEvent("age_13_18", recommendedAge = makeAge(13, 18))
        val age_all = makeEvent("age_all", recommendedAge = makeAge(null, null))

        // INFANT -> age 1 year and below
        assertThat(service.search(age = AttendeeAge.INFANT)).containsExactlyInAnyOrder(age_0_5, age_0_7, age_all)

        // CHILD -> ages 6 - 8
        assertThat(service.search(age = AttendeeAge.CHILD)).containsExactlyInAnyOrder(age_0_7, age_5_10, age_7_up, age_all)

        // TEEN -> ages 15 - 17
        assertThat(service.search(age = AttendeeAge.TEEN)).containsExactlyInAnyOrder(age_7_up, age_13_18, age_all)

        // ADULT -> ages 21 and up
        assertThat(service.search(age = AttendeeAge.ADULT)).containsExactlyInAnyOrder(age_7_up, age_all)
    }

    @Test
    fun `search content`() {
        val a = makeEvent("a", content = "The quick brown.")
        val b = makeEvent("b", content = "Fox jumped over.")
        val c = makeEvent("c", content = "The lazy dog.")

        assertThat(service.search(q = "quick")).containsExactlyInAnyOrder(a)
        assertThat(service.search(q = "the")).containsExactlyInAnyOrder(a, c)
        assertThat(service.search(q = "cupcakes")).isEmpty()
    }

}