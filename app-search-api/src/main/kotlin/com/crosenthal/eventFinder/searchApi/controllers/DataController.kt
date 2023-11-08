package com.crosenthal.eventFinder.searchApi.controllers

import com.crosenthal.eventFinder.locations.LocationService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/data")
class DataController(
    val locationService: LocationService
) {

    // TODO - this isn't ready for use
    //@GetMapping("/locations")
    @Operation(summary = "List known locations")
    fun locations() : Map<String, String> {
        return locationService.getAllLocations()
    }

}