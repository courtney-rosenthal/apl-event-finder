package com.crosenthal.eventFinder.elasticsearch.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

internal class CalendarEventTest {

    private val TEST_EVENT = CalendarEvent(
        url = "test url",
        content = "content",
        isDeleted = true,
        title = "title",
        subTitle = "subtitle",
        summary = "summary",
        description = "description",
        recommendedAge = RecommendedAge(0, 99),
        time = EventDateTime(Instant.now(), end = null, localHourOfDay = 10, localDayOfWeek = "Wed"),
        location = EventLocation("key", "detail"),
        registrationUrl = "registration url",
        isFree = false,
        tags = setOf("AAA", "BBB", "CCC")
    )

    @Test
    fun `test build CalendarEvent successful`() {
        val b = CalendarEvent.Builder(TEST_EVENT.url, TEST_EVENT.content)
        b.isDeleted = TEST_EVENT.isDeleted
        b.title = TEST_EVENT.title
        b.subTitle = TEST_EVENT.subTitle
        b.summary = TEST_EVENT.summary
        b.description = TEST_EVENT.description
        b.recommendedAge = TEST_EVENT.recommendedAge
        b.time = TEST_EVENT.time
        b.location = TEST_EVENT.location
        b.registrationUrl = TEST_EVENT.registrationUrl
        b.isFree = TEST_EVENT.isFree
        b.tags = TEST_EVENT.tags

        val issues = ScrapeIssues(TEST_EVENT.url, "event source")
        val event = b.build(issues)!!

        assertThat(issues.hasIssues).isFalse()
        assertThat(event)
            .usingRecursiveComparison()
            .ignoringFields("timestamp")
            .isEqualTo(TEST_EVENT)
        assertThat(event.timestamp).isAfterOrEqualTo(TEST_EVENT.timestamp)
    }

    @Test
    fun `test build CalendarEvent failed`() {
        val issues = ScrapeIssues(TEST_EVENT.url, "event source")
        val event = CalendarEvent.Builder(TEST_EVENT.url, TEST_EVENT.content).build(issues)

        assertThat(event).isNull()
        assertThat(issues.hasIssues).isTrue()
        assertThat(issues.issues).hasSize(4)
        val messages = issues.issues.map { it.message }
        assertThat(messages).containsExactlyInAnyOrder(
            "could not extract title for event",
            "could not extract description for event",
            "could not extract time for event",
            "could not extract location for event",
        )
    }

}