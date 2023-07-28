package com.crosenthal.eventFinder.searchApi.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class HelloController {
    @GetMapping
    fun greeting(@RequestParam(defaultValue = "world") target: String): String {
        return "hello $target\n"
    }
}