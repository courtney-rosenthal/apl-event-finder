package com.crosenthal.eventFinder.elasticsearch

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "application.elasticsearch")
open class ElasticsearchProperties() {
    var indexPrefix: String = ""
}
