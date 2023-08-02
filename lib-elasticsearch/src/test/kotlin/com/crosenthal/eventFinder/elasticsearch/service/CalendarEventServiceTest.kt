package com.crosenthal.eventFinder.elasticsearch.service

import com.crosenthal.eventFinder.elasticsearch.repository.CalendarEventRepository
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach


internal class CalendarEventServiceTest {
    lateinit var repository: CalendarEventRepository
    lateinit var service: CalendarEventService

    @BeforeEach
    fun setup() {
        repository = mockk<CalendarEventRepository>()
        service = CalendarEventService(repository)
    }

//    private fun makeCalendarEvent(): CalendarEvent {
//        return CalendarEvent.Builder(
//            url = "http://example.com.document",
//            content = "<p>The quick brown fox.</p>"
//        ).build()
//
//    }
//
//    @Test
//    fun save() { // TODO - move to repository test
//        val event = makeCalendarEvent()
//        every { repository.save(event) } returns event
//        val result = service.repository.save(event)
//        assertThat(result).isEqualTo(event)
//    }
//
//    @Test
//    fun `findById found`() { // TODO - move to repository test
//        val event = makeCalendarEvent()
//        every { repository.findById(event.url) } returns Optional.of(event)
//        val result = service.repository.findById(event.url)
//        assertThat(result).isEqualTo(event)
//    }
//
//    @Test
//    fun `findById not found`() { // TODO - move to repository test
//        every { repository.findById(any()) } returns Optional.empty()
//        val result = service.findById("bloop")
//        assertThat(result).isNull()
//    }

}