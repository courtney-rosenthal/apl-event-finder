package com.crosenthal.eventFinder.elasticsearch.service

import com.crosenthal.eventFinder.elasticsearch.ElasticsearchConfig
import com.crosenthal.eventFinder.elasticsearch.ElasticsearchProperties
import com.crosenthal.eventFinder.elasticsearch.TestUtil
import com.crosenthal.eventFinder.elasticsearch.domain.CalendarEvent
import com.crosenthal.eventFinder.elasticsearch.domain.EventDateTime
import com.crosenthal.eventFinder.elasticsearch.domain.EventLocation
import com.crosenthal.eventFinder.elasticsearch.domain.RecommendedAge
import com.crosenthal.eventFinder.elasticsearch.misc.CalendarEventSearchCriteria
import com.crosenthal.eventFinder.elasticsearch.misc.CalendarEventSearchCriteria.AttendeeAge
import com.crosenthal.eventFinder.elasticsearch.misc.CalendarEventSearchCriteria.Day
import com.crosenthal.eventFinder.elasticsearch.misc.CalendarEventSearchCriteria.Time
import com.crosenthal.eventFinder.elasticsearch.repository.CalendarEventRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.IterableAssert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID


private typealias _C = CalendarEventSearchCriteria

@SpringBootTest(classes = [ElasticsearchProperties::class, ElasticsearchConfig::class, CalendarEventService::class])
@EnableConfigurationProperties(ElasticsearchProperties::class)
@Suppress("UNUSED_VARIABLE") // there's a bunch of unused variables, just ignore them
internal class CalendarEventServiceIntegrationTest {

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
        location: EventLocation? = null,
        tags: Set<String> = emptySet(),
        isDeleted: Boolean = false,
    ): CalendarEvent {
        val event = TestUtil.makeEvent(
            url = url,
            content = content,
            time = time,
            recommendedAge = recommendedAge,
            location = location,
            tags = tags,
            isDeleted = isDeleted,
        )
        return repository.save(event)
    }

    private fun makeTime(date: String = "2023-01-01", startTime: String = "12:00", endTime: String = "13:00"): EventDateTime {
        return EventDateTime.of(LocalDate.parse(date), LocalTime.parse(startTime), LocalTime.parse(endTime))
    }

    private fun makeAge(minYears: Int?, maxYears: Int?) : RecommendedAge {
        return RecommendedAge(minYears, maxYears)
    }

    private fun assertThatSearchResults(criteria: CalendarEventSearchCriteria): IterableAssert<CalendarEvent> {
        val results = service.search(criteria).searchHits.map {it.content }
        return assertThat(results)
    }

    @Test
    fun `search with no arguments returns all events`() {
        val a = makeEvent("a")
        val b = makeEvent("b")
        assertThatSearchResults(_C()).containsExactlyInAnyOrder(a, b)
    }


    @Test
    fun `search by localDayOfWeek`() {
        val sat = makeEvent("sat", time = makeTime(date = "2023-07-08"))
        val sun = makeEvent("sun", time = makeTime(date = "2023-07-09"))
        val mon = makeEvent("mon", time = makeTime(date = "2023-07-10"))
        val tue = makeEvent("tue", time = makeTime(date = "2023-07-11"))


        assertThatSearchResults(_C(days = setOf(Day.SAT))).containsExactlyInAnyOrder(sat)
        assertThatSearchResults(_C(days = setOf(Day.MON))).containsExactlyInAnyOrder(mon)
        assertThatSearchResults(_C(days = setOf(Day.SAT, Day.MON))).containsExactlyInAnyOrder(sat, mon)
        assertThatSearchResults(_C(days = setOf(Day.WEEKDAY))).containsExactlyInAnyOrder(mon, tue)
        assertThatSearchResults(_C(days = setOf(Day.WEEKEND))).containsExactlyInAnyOrder(sat, sun)
    }

    @Test
    fun `search by localHourOfDay`() {
        val morning = makeEvent("morning", time = makeTime(startTime = "10:00"))
        val afternoon = makeEvent("afternoon", time = makeTime(startTime = "13:00"))
        val evening = makeEvent("evening", time = makeTime(startTime = "19:00"))

        assertThatSearchResults(_C(times = setOf(Time.MORNING))).containsExactlyInAnyOrder(morning)
        assertThatSearchResults(_C(times = setOf(Time.EVENING))).containsExactlyInAnyOrder(evening)
        assertThatSearchResults(_C(times = setOf(Time.EVENING, Time.MORNING))).containsExactlyInAnyOrder(morning, evening)
    }

    @Test
    fun `search by locations`() {
        val a1 = makeEvent(url = "a1", location = EventLocation(key = "AAA", detail = "Location AAA"))
        val a2 = makeEvent(url = "a2", location = EventLocation(key = "AAA", detail = "Location AAA"))
        val b1 = makeEvent(url = "b1", location = EventLocation(key = "BBB", detail = "Location BBB"))
        val b2 = makeEvent(url = "b2", location = EventLocation(key = "BBB", detail = "Location BBB"))
        val c1 = makeEvent(url = "c1", location = EventLocation(key = "CCC", detail = "Location CCC"))
        val x1 = makeEvent(url = "x1", location = EventLocation(key = null, detail = "Location XXX"))

        assertThatSearchResults(_C(locations = setOf("AAA"))).containsExactlyInAnyOrder(a1, a2)
        assertThatSearchResults(_C(locations = setOf("AAA", "BBB"))).containsExactlyInAnyOrder(a1, a2, b1, b2)
        assertThatSearchResults(_C(locations = setOf("AAA", "ZZZ"))).containsExactlyInAnyOrder(a1, a2)
        assertThatSearchResults(_C(locations = setOf("ZZZ"))).isEmpty()
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
        assertThatSearchResults(_C(age = AttendeeAge.INFANT)).containsExactlyInAnyOrder(age_0_5, age_0_7, age_all)

        // CHILD -> ages 6 - 8
        assertThatSearchResults(_C(age = AttendeeAge.CHILD)).containsExactlyInAnyOrder(age_0_7, age_5_10, age_7_up, age_all)

        // TEEN -> ages 15 - 17
        assertThatSearchResults(_C(age = AttendeeAge.TEEN)).containsExactlyInAnyOrder(age_7_up, age_13_18, age_all)

        // ADULT -> ages 21 and up
        assertThatSearchResults(_C(age = AttendeeAge.ADULT)).containsExactlyInAnyOrder(age_7_up, age_all)
    }

    @Test
    fun `search by tags`() {
        val (TAG1, TAG2, TAG3, TAG4) = arrayOf("tag1", "tag2", "tag3", "tag4")
        val t0 = makeEvent("t0", tags = emptySet())
        val t1 = makeEvent("t1", tags = setOf(TAG1) )
        val t23 = makeEvent("t23", tags = setOf(TAG2, TAG3) )
        val t3 = makeEvent("t3", tags = setOf(TAG3) )

        assertThatSearchResults(_C(tags = setOf(TAG1))).containsExactlyInAnyOrder(t1)
        assertThatSearchResults(_C(tags = setOf(TAG1, TAG2))).containsExactlyInAnyOrder(t1, t23)
        assertThatSearchResults(_C(tags = setOf(TAG4))).isEmpty()
        assertThatSearchResults(_C(tags = setOf(TAG1, TAG4))).containsExactlyInAnyOrder(t1)
    }

    @Test
    fun `search content`() {
        val a = makeEvent("a", content = "The quick brown.")
        val b = makeEvent("b", content = "Fox jumped over.")
        val c = makeEvent("c", content = "The lazy dog.")

        assertThatSearchResults(_C(searchText = "quick")).containsExactlyInAnyOrder(a)
        assertThatSearchResults(_C(searchText = "the")).containsExactlyInAnyOrder(a, c)
        assertThatSearchResults(_C(searchText = "cupcakes")).isEmpty()
    }

    @Test
    fun `search excludes deleted events`() {
        val a = makeEvent("a", isDeleted = false)
        val b = makeEvent("b", isDeleted = true)
        assertThatSearchResults(_C()).containsExactlyInAnyOrder(a)
    }

    @Test
    fun `verify pagination works`() {

        // no matches found
        with (service.search(_C(), pageSize = 5)) {
            assertThat(this.totalElements).isEqualTo(0)
            assertThat(this.totalPages).isEqualTo(0)
            assertThat(this.isEmpty).isTrue()
            assertThat(this.isFirst).isTrue()
            assertThat(this.isLast).isTrue()
            assertThat(this.number).isEqualTo(0)
            assertThat(this.numberOfElements).isEqualTo(0)
            assertThat(this.searchHits.searchHits).isEmpty()
        }


        val t = Instant.now()
        (0..99).forEach {
            makeEvent(
                "event%02d".format(it),
                time = EventDateTime(start = t.plusSeconds(it.toLong()), end = null, localHourOfDay = 0, localDayOfWeek = "X")
            )
        }

        // retrieve first page of matches
        with (service.search(_C(), pageSize = 5)) {
            assertThat(this.totalElements).isEqualTo(100)
            assertThat(this.totalPages).isEqualTo(20)
            assertThat(this.isEmpty).isFalse()
            assertThat(this.isFirst).isTrue()
            assertThat(this.isLast).isFalse()
            assertThat(this.number).isEqualTo(0)
            assertThat(this.numberOfElements).isEqualTo(5)
            assertThat(this.searchHits.searchHits.first().content.url).isEqualTo("event00")
        }

        // retrieve second page of matches
        with (service.search(_C(), pageSize = 5, pageNum = 1)) {
            assertThat(this.totalElements).isEqualTo(100)
            assertThat(this.totalPages).isEqualTo(20)
            assertThat(this.isEmpty).isFalse()
            assertThat(this.isFirst).isFalse()
            assertThat(this.isLast).isFalse()
            assertThat(this.number).isEqualTo(1)
            assertThat(this.numberOfElements).isEqualTo(5)
            assertThat(this.searchHits.searchHits.first().content.url).isEqualTo("event05")
        }

        // retrieve last page of matches
        with (service.search(_C(), pageSize = 5, pageNum = 19)) {
            assertThat(this.totalElements).isEqualTo(100)
            assertThat(this.totalPages).isEqualTo(20)
            assertThat(this.isEmpty).isFalse()
            assertThat(this.isFirst).isFalse()
            assertThat(this.isLast).isTrue()
            assertThat(this.number).isEqualTo(19)
            assertThat(this.numberOfElements).isEqualTo(5)
            assertThat(this.searchHits.searchHits.first().content.url).isEqualTo("event95")
        }

        // retrieve page beyond end of matches
        with (service.search(_C(), pageSize = 5, pageNum = 99)) {
            assertThat(this.totalElements).isEqualTo(100)
            assertThat(this.totalPages).isEqualTo(20)
            assertThat(this.isEmpty).isTrue()
            assertThat(this.isFirst).isFalse()
            assertThat(this.isLast).isTrue()
            assertThat(this.number).isEqualTo(99)
            assertThat(this.numberOfElements).isEqualTo(0)
            assertThat(this.searchHits.searchHits).isEmpty()
        }

    }

    @Test
    fun listTags() {
        val (TAG1, TAG2, TAG3, TAG4) = arrayOf("tag1", "tag2", "tag3", "tag4")
        makeEvent("t0", tags = emptySet())
        assertThat(service.listTags()).isEmpty()

        makeEvent("t1", tags = setOf(TAG1))
        makeEvent("t2", tags = setOf(TAG2))
        makeEvent("t12", tags = setOf(TAG1, TAG2))
        makeEvent("t13", tags = setOf(TAG1, TAG3))
        assertThat(service.listTags()).containsExactlyInAnyOrder(TAG1, TAG2, TAG3)
    }

}