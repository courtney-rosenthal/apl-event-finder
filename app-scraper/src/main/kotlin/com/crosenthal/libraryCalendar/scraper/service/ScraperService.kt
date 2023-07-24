package com.crosenthal.libraryCalendar.scraper.service

import com.crosenthal.libraryCalendar.elasticsearch.repository.CalendarEventRepository
import com.crosenthal.libraryCalendar.elasticsearch.repository.ScrapeIssuesRepository
import com.crosenthal.libraryCalendar.scraper.ScraperApplication
import com.crosenthal.libraryCalendar.scraper.config.ApplicationProperties
import org.fissore.slf4j.FluentLoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.net.URL

@Service
class ScraperService(
    val eventsFeed: EventsFeed,
    val eventScraper: EventScraper,
    val calendarEventRepository: CalendarEventRepository,
    val scrapeIssuesRepository: ScrapeIssuesRepository,
    @Qualifier("applicationProperties") val config: ApplicationProperties
) {

    companion object {
        val LOG = FluentLoggerFactory.getLogger(ScraperApplication::class.java)
    }

    fun scrapeOneLink(url: String) {
        val doc = eventScraper.loadDocumentFromUrl(url)
        val (event, issues) = eventScraper.scrapeToEvent(doc, url)
        calendarEventRepository.save(event)
        scrapeIssuesRepository.save(issues)
    }

    fun performFullSrape() {
        LOG.info().log("performScrape: starting")
        eventsFeed.extractLinksFromFeed(URL(config.eventsRss))
            .take(10)
            .forEach {
                scrapeOneLink(it)
            }
        LOG.info().log("performScrape: finished")
    }

}