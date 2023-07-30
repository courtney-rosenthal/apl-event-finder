package com.crosenthal.eventFinder.searchApi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@SpringBootApplication(scanBasePackages = ["com.crosenthal.eventFinder"])
open class SearchApiApplication

fun main(args: Array<String>) {
    runApplication<SearchApiApplication>(*args)
}


@Configuration
@EnableWebMvc
open class WebConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
    }
}