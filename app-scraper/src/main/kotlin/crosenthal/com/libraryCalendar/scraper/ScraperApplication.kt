package crosenthal.com.libraryCalendar.scraper

import crosenthal.com.libraryCalendar.scraper.service.ScraperService
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationListener


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
