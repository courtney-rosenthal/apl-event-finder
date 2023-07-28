package com.crosenthal.eventFinder.searchApi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.crosenthal.eventFinder"])
open class SearchApiApplication

fun main(args: Array<String>) {
    runApplication<SearchApiApplication>(*args)
}