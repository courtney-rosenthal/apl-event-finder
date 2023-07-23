package crosenthal.com.libraryCalendar.scraper

import crosenthal.com.libraryCalendar.scraper.config.ApplicationProperties
import crosenthal.com.libraryCalendar.scraper.service.EventScraper
import crosenthal.com.libraryCalendar.scraper.service.EventsFeed
import crosenthal.com.libraryCalendar.scraper.service.ScraperService
import org.fissore.slf4j.FluentLoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct


@SpringBootApplication
class Application(
	val applicationContext: ApplicationContext,
	val scraperService: ScraperService
)  : ApplicationListener<ApplicationReadyEvent> {
	override fun onApplicationEvent(event: ApplicationReadyEvent) {
		scraperService.performFullSrape()
		SpringApplication.exit(applicationContext)
	}
}

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}
