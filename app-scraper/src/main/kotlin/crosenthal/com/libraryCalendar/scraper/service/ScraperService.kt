package crosenthal.com.libraryCalendar.scraper.service

import crosenthal.com.libraryCalendar.scraper.Application
import crosenthal.com.libraryCalendar.scraper.config.ApplicationProperties
import crosenthal.com.libraryCalendar.scraper.repository.CalendarEventRepository
import crosenthal.com.libraryCalendar.scraper.repository.ScrapeIssuesRepository
import org.fissore.slf4j.FluentLoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.net.URL
import javax.annotation.PostConstruct

@Service
class ScraperService(
    val eventsFeed: EventsFeed,
    val eventScraper: EventScraper,
    val calendarEventRepository: CalendarEventRepository,
    val scrapeIssuesRepository: ScrapeIssuesRepository,
    @Qualifier("applicationProperties") val config: ApplicationProperties
) {

    companion object {
        val LOG = FluentLoggerFactory.getLogger(Application::class.java)
    }

    fun scrapeOneLink(url: String) {
        val doc = eventScraper.loadDocumentFromUrl(url)
        val (event, issues) = eventScraper.scrapeToEvent(doc, url)
        calendarEventRepository.save(event)
        scrapeIssuesRepository.save(issues)
    }

    fun performFullSrape() {
        LOG.info().log("performScrape: starting")
        val links = eventsFeed.extractLinksFromFeed(URL(config.eventsRss))
        links
            .take(if (config.maxPagesScraped > 0) config.maxPagesScraped else Int.MAX_VALUE)
            .forEach { scrapeOneLink(it) }
        LOG.info().log("performScrape: finished")
    }

}