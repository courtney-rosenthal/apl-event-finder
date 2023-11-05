package com.crosenthal.eventFinder.scraper.service

import com.crosenthal.eventFinder.elasticsearch.domain.ScrapeIssues
import com.crosenthal.eventFinder.elasticsearch.service.CalendarEventService
import com.crosenthal.eventFinder.elasticsearch.service.ScrapeIssuesService
import com.crosenthal.eventFinder.scraper.ScraperApplication
import com.crosenthal.eventFinder.scraper.config.ApplicationProperties
import org.fissore.slf4j.FluentLoggerFactory
import org.jsoup.HttpStatusException
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
        val doc = try {
             eventScraper.loadDocumentFromUrl(url)
        } catch (ex: HttpStatusException) {
            val mssg = "Exception while retrieving document: " + ex.message
            LOG.warn().log(mssg)
            val issues = ScrapeIssues(url, "")
            issues.add(mssg, "skipped")
            scrapeIssuesService.repository.save(issues)
            return
        }

        val (event, issues) = eventScraper.scrapeToEvent(doc, url)
        if (event != null) {
            calendarEventService.repository.save(event)
        }

        if (issues.hasIssues) {
            scrapeIssuesService.repository.save(issues)
        } else {
            scrapeIssuesService.repository.deleteById(issues.url)
        }
        LOG.debug().log("scrapeAndSaveOneLink: saved event url={}", url)
    }

    fun performFullSrapeAndSave(maxScrape: Int?) {
        LOG.info().log("performScrape: starting")
        eventsFeed.extractLinksFromFeed(URL(config.eventsRss))
            .take(maxScrape ?: 999)
            .forEach {
                scrapeAndSaveOneLink(it)
            }
        LOG.info().log("performScrape: finished")
    }

}