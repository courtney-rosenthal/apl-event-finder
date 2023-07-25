package com.crosenthal.libraryCalendar.scraper.service

import com.crosenthal.libraryCalendar.elasticsearch.service.CalendarEventService
import com.crosenthal.libraryCalendar.elasticsearch.service.ScrapeIssuesService
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
    val calendarEventService: CalendarEventService,
    val scrapeIssuesService: ScrapeIssuesService,
    @Qualifier("applicationProperties") val config: ApplicationProperties
) {

    companion object {
        val LOG = FluentLoggerFactory.getLogger(ScraperApplication::class.java)
    }

    fun scrapeAndSaveOneLink(url: String) {
        val doc = eventScraper.loadDocumentFromUrl(url)
        val (event, issues) = eventScraper.scrapeToEvent(doc, url)
        calendarEventService.save(event)
        scrapeIssuesService.save(issues)
        LOG.debug().log("scrapeAndSaveOneLink: saved event url={}", url)
    }

    fun performFullSrapeAndSave() {
        LOG.info().log("performScrape: starting")
        eventsFeed.extractLinksFromFeed(URL(config.eventsRss))
            .take(10)
            .forEach {
                scrapeAndSaveOneLink(it)
            }
        LOG.info().log("performScrape: finished")
    }

}