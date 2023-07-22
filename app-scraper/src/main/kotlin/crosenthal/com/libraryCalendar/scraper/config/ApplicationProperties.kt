package crosenthal.com.libraryCalendar.scraper.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "application")
@ConfigurationPropertiesScan
class ApplicationProperties {
    public var eventsRss: String = "https://library.austintexas.gov/events-feed.xml"
}