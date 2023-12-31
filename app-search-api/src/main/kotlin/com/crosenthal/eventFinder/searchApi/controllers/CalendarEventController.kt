package com.crosenthal.eventFinder.searchApi.controllers

import com.crosenthal.eventFinder.elasticsearch.domain.CalendarEvent
import com.crosenthal.eventFinder.elasticsearch.misc.CalendarEventSearchCriteria
import com.crosenthal.eventFinder.elasticsearch.misc.CalendarEventSearchCriteria.AttendeeAge
import com.crosenthal.eventFinder.elasticsearch.misc.CalendarEventSearchCriteria.Day
import com.crosenthal.eventFinder.elasticsearch.misc.CalendarEventSearchCriteria.Time
import com.crosenthal.eventFinder.elasticsearch.misc.SearchPageResponse
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
        return service.repository.findById(url).orElseThrow{ EntityNotFound() }
    }

    @GetMapping("/search")
    @Operation(summary = "Retrieve events based on search criteria, sorted in chronological ascending order")
    fun search(
        @RequestParam days: Set<Day>?,
        @RequestParam times: Set<Time>?,
        @RequestParam locations: Set<String>?,
        @RequestParam age: AttendeeAge?,
        @RequestParam tags: Set<String>?,
        @RequestParam searchText: String?,
        @RequestParam pageSize: Int?,
        @RequestParam pageNum: Int?,
    ) : SearchPageResponse<CalendarEvent> {
        val criteria = CalendarEventSearchCriteria(
            days = days,
            times = times,
            locations = locations,
            age = age,
            tags = tags,
            searchText = searchText
        )
        return search(criteria, pageSize, pageNum)
    }

    @PostMapping("/search")
    @Operation(summary = "Retrieve events based on search criteria, sorted in chronological ascending order")
    fun search(
        @RequestBody criteria: CalendarEventSearchCriteria,
        @RequestParam pageSize: Int?,
        @RequestParam pageNum: Int?,
    ) : SearchPageResponse<CalendarEvent> {
        return SearchPageResponse.build(service.search(criteria, pageSize, pageNum))
    }


    @GetMapping("/tags")
    @Operation(summary = "Retrieve all tags used in current events, sorted in alpha ascending order")
    fun tags() : List<String> {
        return service.listTags()
    }

}