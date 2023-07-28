package com.crosenthal.eventFinder.elasticsearch.service

import com.crosenthal.eventFinder.elasticsearch.repository.CalendarEventRepository
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Optional


internal class CalendarEventServiceTest {
    lateinit var repository: CalendarEventRepository
    lateinit var service: CalendarEventService

    @BeforeEach
    fun setup() {
        repository = mockk<CalendarEventRepository>()
        service = CalendarEventService(repository)
    }

    private fun makeCalendarEvent(): com.crosenthal.eventFinder.elasticsearch.domain.CalendarEvent {
        return com.crosenthal.eventFinder.elasticsearch.domain.CalendarEvent(
            url = "http://example.com.document",
            content = "<p>The quick brown fox.</p>"
        )

    }

    @Test
    fun save() {
        val event = makeCalendarEvent()
        every { repository.save(event) } returns event
        val result = service.save(event)
        assertThat(result).isEqualTo(event)
    }

    @Test
    fun `findById found`() {
        val event = makeCalendarEvent()
        every { repository.findById(event.url) } returns Optional.of(event)
        val result = service.findById(event.url)
        assertThat(result).isEqualTo(event)
    }

    @Test
    fun `findById not found`() {
        val event = makeCalendarEvent()
        every { repository.findById(any()) } returns Optional.empty()
        val result = service.findById("bloop")
        assertThat(result).isNull()
    }

}