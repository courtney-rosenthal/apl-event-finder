package com.crosenthal.libraryCalendar.searchApi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.crosenthal.libraryCalendar"])
open class SearchApiApplication

fun main(args: Array<String>) {
    runApplication<SearchApiApplication>(*args)
}