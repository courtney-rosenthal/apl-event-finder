package com.crosenthal.libraryCalendar.searchApi.controllers

import com.crosenthal.libraryCalendar.elasticsearch.domain.CalendarEvent
import com.crosenthal.libraryCalendar.elasticsearch.service.CalendarEventService
import com.crosenthal.libraryCalendar.searchApi.exceptions.EntityNotFound
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/calendarEvent")
class CalendarEventsController(
    val service: CalendarEventService
) {

    @GetMapping
    fun get(@RequestParam url: String): CalendarEvent {
        return service.findById(url) ?: throw EntityNotFound()
    }

    @GetMapping("/search")
    fun search(
        @RequestParam q: String?
    ) : List<CalendarEvent> {
        return service.search(q)
    }
}