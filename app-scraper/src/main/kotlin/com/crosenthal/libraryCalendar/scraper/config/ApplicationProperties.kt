package com.crosenthal.libraryCalendar.scraper.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "application")
@ConfigurationPropertiesScan
open class ApplicationProperties {
    var eventsRss: String = "https://library.austintexas.gov/events-feed.xml"
    var coerceToHttps: Boolean = true
    var maxPagesScraped: Int = 10
}