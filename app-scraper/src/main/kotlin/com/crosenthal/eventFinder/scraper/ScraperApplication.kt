package com.crosenthal.eventFinder.scraper

import com.crosenthal.eventFinder.scraper.config.ApplicationProperties
import com.crosenthal.eventFinder.scraper.service.ScraperService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationListener


@SpringBootApplication(scanBasePackages = ["com.crosenthal.eventFinder"])
open class ScraperApplication(
	val applicationContext: ApplicationContext,
	@Qualifier("applicationProperties") val props: ApplicationProperties,
	val scraperService: ScraperService
)  : ApplicationListener<ApplicationReadyEvent> {
	override fun onApplicationEvent(event: ApplicationReadyEvent) {
		scraperService.performFullSrapeAndSave(props.maxEventsToScrape)
		SpringApplication.exit(applicationContext)
	}
}

fun main(args: Array<String>) {
	runApplication<ScraperApplication>(*args)
}
