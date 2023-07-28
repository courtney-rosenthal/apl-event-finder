package com.crosenthal.libraryCalendar.searchApi.controllers

import com.crosenthal.libraryCalendar.elasticsearch.domain.CalendarEvent
import com.crosenthal.libraryCalendar.elasticsearch.misc.SearchConditions
import com.crosenthal.libraryCalendar.elasticsearch.service.CalendarEventService
import com.crosenthal.libraryCalendar.searchApi.exceptions.EntityNotFound
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/calendarEvent")
class CalendarEventController(
    val service: CalendarEventService
) {

    @GetMapping
    @Operation(summary = "Retrieve a single event by URL")
    fun get(@RequestParam url: String): CalendarEvent {
        return service.findById(url) ?: throw EntityNotFound()
    }

    @GetMapping("/search")
    @Operation(summary = "Search for events")
    fun search(
        @RequestParam days: Set<SearchConditions.Day>?,
        @RequestParam times: Set<SearchConditions.Time>?,
        @RequestParam branches: Set<SearchConditions.Branch>?,
        @RequestParam attendeeAge: SearchConditions.AttendeeAge?,
        @RequestParam q: String?
    ) : List<CalendarEvent> {
        return service.search(
            days = days,
            times = times,
            branches = branches,
            age = attendeeAge,
            q = q
        )
    }
}