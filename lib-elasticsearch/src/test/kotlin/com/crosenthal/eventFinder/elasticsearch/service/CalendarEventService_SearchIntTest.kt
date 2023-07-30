package com.crosenthal.eventFinder.elasticsearch.service

import com.crosenthal.eventFinder.elasticsearch.domain.EventDateTime
import com.crosenthal.eventFinder.elasticsearch.domain.RecommendedAge
import com.crosenthal.eventFinder.elasticsearch.misc.AttendeeAge
import com.crosenthal.eventFinder.elasticsearch.misc.Day
import com.crosenthal.eventFinder.elasticsearch.misc.Time
import com.crosenthal.eventFinder.elasticsearch.repository.CalendarEventRepository
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


@SpringBootTest(classes = [com.crosenthal.eventFinder.elasticsearch.ElasticsearchProperties::class, com.crosenthal.eventFinder.elasticsearch.ElasticsearchConfig::class, CalendarEventService::class])
@EnableConfigurationProperties(com.crosenthal.eventFinder.elasticsearch.ElasticsearchProperties::class)
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
        recommendedAge: RecommendedAge? = null,
        tags: Set<String> = emptySet(),
    ): com.crosenthal.eventFinder.elasticsearch.domain.CalendarEvent {
        return repository.save(
            com.crosenthal.eventFinder.elasticsearch.domain.CalendarEvent(
                url = url,
                content = content,
                time = time,
                recommendedAge = recommendedAge,
                tags = tags,
            )
        )
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
    fun `search by tags`() {
        val (TAG1, TAG2, TAG3, TAG4) = arrayOf("tag1", "tag2", "tag3", "tag4")
        val t0 = makeEvent("t0", tags = emptySet())
        val t1 = makeEvent("t1", tags = setOf(TAG1) )
        val t23 = makeEvent("t23", tags = setOf(TAG2, TAG3) )
        val t3 = makeEvent("t3", tags = setOf(TAG3) )

        assertThat(service.search(tags = setOf(TAG1))).containsExactlyInAnyOrder(t1)
        assertThat(service.search(tags = setOf(TAG1, TAG2))).containsExactlyInAnyOrder(t1, t23)
        assertThat(service.search(tags = setOf(TAG4))).isEmpty()
        assertThat(service.search(tags = setOf(TAG1, TAG4))).containsExactlyInAnyOrder(t1)
    }

    @Test
    fun `search content`() {
        val a = makeEvent("a", content = "The quick brown.")
        val b = makeEvent("b", content = "Fox jumped over.")
        val c = makeEvent("c", content = "The lazy dog.")

        assertThat(service.search(searchText = "quick")).containsExactlyInAnyOrder(a)
        assertThat(service.search(searchText = "the")).containsExactlyInAnyOrder(a, c)
        assertThat(service.search(searchText = "cupcakes")).isEmpty()
    }

}