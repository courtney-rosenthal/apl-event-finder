package com.crosenthal.eventFinder.searchApi.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/ping")
class PingController {
    @GetMapping
    fun ping(@RequestParam response: String?): String {
        return (response ?: "pong") + "\n"
    }
}