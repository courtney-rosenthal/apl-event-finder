package com.crosenthal.eventFinder.elasticsearch

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "application.elasticsearch")
open class ElasticsearchProperties() {
    var indexPrefix: String = ""
    var serverHostAndPort: String = "localhost:9200"
    // ssl and auth have not been tested
    var useSSL: Boolean = false
    var authUsername: String? = null
    var authPassword: String? = null
}
