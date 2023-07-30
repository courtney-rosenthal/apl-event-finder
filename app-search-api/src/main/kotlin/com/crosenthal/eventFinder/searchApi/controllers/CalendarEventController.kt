package com.crosenthal.eventFinder.searchApi.controllers

import com.crosenthal.eventFinder.elasticsearch.domain.CalendarEvent
import com.crosenthal.eventFinder.elasticsearch.misc.AttendeeAge
import com.crosenthal.eventFinder.elasticsearch.misc.Branch
import com.crosenthal.eventFinder.elasticsearch.misc.CalendarEventSearchCriteria
import com.crosenthal.eventFinder.elasticsearch.misc.Day
import com.crosenthal.eventFinder.elasticsearch.misc.Time
import com.crosenthal.eventFinder.elasticsearch.service.CalendarEventService
import com.crosenthal.eventFinder.searchApi.exceptions.EntityNotFound
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
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
        @RequestParam days: Set<Day>?,
        @RequestParam times: Set<Time>?,
        @RequestParam branches: Set<Branch>?,
        @RequestParam attendeeAge: AttendeeAge?,
        @RequestParam tags: Set<String>?,
        @RequestParam searchText: String?
    ) : List<CalendarEvent> {
        return service.search(
            days = days,
            times = times,
            branches = branches,
            age = attendeeAge,
            tags = tags ?: emptySet(),
            searchText = searchText ?: ""
        )
    }

    @PostMapping("/search")
    @Operation(summary = "Search for events")
    fun search(@RequestBody criteria: CalendarEventSearchCriteria) : List<CalendarEvent> {
        return service.search(criteria)
    }
}