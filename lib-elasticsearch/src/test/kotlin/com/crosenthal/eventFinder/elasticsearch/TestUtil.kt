package com.crosenthal.eventFinder.elasticsearch

import com.crosenthal.eventFinder.elasticsearch.domain.CalendarEvent
import com.crosenthal.eventFinder.elasticsearch.domain.EventDateTime
import com.crosenthal.eventFinder.elasticsearch.domain.RecommendedAge
import io.mockk.mockk
import java.time.LocalDate
import java.time.LocalTime

object TestUtil {

    val EVENT_URL = "http://example.com/document.html"
    val EVENT_CONTENT = "<div>the quick brown fox</div>"
    val EVENT_TITLE = "Event Title"
    val EVENT_DESCRIPTION = "Event description."
    val EVENT_TIME = EventDateTime.of(date = LocalDate.now(), startTime = LocalTime.now(), endTime = null)
    val EVENT_LOCATION = "Event location"


    fun makeEvent(
        // required fields in the CalendarEvent
        url: String = EVENT_URL,
        content: String =  EVENT_CONTENT,
        title: String? = null,
        description: String? = null,
        time: EventDateTime? = null,
        location: String? = null,
        // optional fields in the CalendarEvent
        recommendedAge: RecommendedAge? = null,
        tags: Set<String> = emptySet(),
    ): CalendarEvent {
        return CalendarEvent.Builder(url = url, content = content ).apply {
            this.title = title ?: EVENT_TITLE
            this.description = description ?: EVENT_DESCRIPTION
            this.time = time ?: EVENT_TIME
            this.location = location ?: EVENT_LOCATION
            this.recommendedAge = recommendedAge
            this.tags = tags
        }.build(mockk())!!
    }

}