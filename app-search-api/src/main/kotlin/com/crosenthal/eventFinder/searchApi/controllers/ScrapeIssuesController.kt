package com.crosenthal.eventFinder.searchApi.controllers

import com.crosenthal.eventFinder.elasticsearch.domain.ScrapeIssues
import com.crosenthal.eventFinder.elasticsearch.service.ScrapeIssuesService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/scrapeIssues")
class ScrapeIssuesController(
    val service: ScrapeIssuesService
) {

    @GetMapping
    @Operation(summary = "Retrieve issues encountered by scrape, in chronological descending order")
    fun list(): List<ScrapeIssues> {
        return service.listIssues()
    }

}