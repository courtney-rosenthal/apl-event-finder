package com.crosenthal.eventFinder.scraper.service

import com.crosenthal.eventFinder.elasticsearch.domain.CalendarEvent
import com.crosenthal.eventFinder.elasticsearch.domain.ScrapeIssues
import com.crosenthal.eventFinder.elasticsearch.repository.CalendarEventRepository
import com.crosenthal.eventFinder.elasticsearch.repository.ScrapeIssuesRepository
import com.crosenthal.eventFinder.elasticsearch.service.CalendarEventService
import com.crosenthal.eventFinder.elasticsearch.service.ScrapeIssuesService
import com.crosenthal.eventFinder.scraper.config.ApplicationProperties
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verifySequence
import org.assertj.core.api.Assertions.assertThat
import org.jsoup.nodes.Document
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class ScraperServiceTest {

    lateinit var service: ScraperService

    val eventsFeed = mockk<EventsFeed>()
    val eventScraper = mockk<EventScraper>()
    val calendarEventService = mockk<CalendarEventService>()
    val scrapeIssuesService = mockk<ScrapeIssuesService>()
    val config = ApplicationProperties()

    @BeforeEach
    internal fun setup() {
        service = ScraperService(
            eventsFeed = eventsFeed,
            eventScraper = eventScraper,
            calendarEventService = calendarEventService,
            scrapeIssuesService = scrapeIssuesService,
            config = config
        )
    }

    @Test
    fun `scrapeAndSaveOneLink successful`() {
        val TEST_URL = "http://test.example/"
        val doc = mockk<Document>()
        every { eventScraper.loadDocumentFromUrl(TEST_URL) } returns doc
        val event = mockk<CalendarEvent>()
        val issues = mockk<ScrapeIssues>()
        every { eventScraper.scrapeToEvent(doc, TEST_URL) } returns Pair(event, issues)
        val calendarEventRepository = mockk<CalendarEventRepository>()
        every { calendarEventService.repository } returns calendarEventRepository
        every { calendarEventRepository.save(event) } returns event
        every { issues.hasIssues() } returns false // *** scrape was successful
        val scrapeIssuesRepository = mockk<ScrapeIssuesRepository>()
        every { scrapeIssuesService.repository } returns scrapeIssuesRepository
        every { issues.url } returns TEST_URL
        every { scrapeIssuesRepository.deleteById(TEST_URL) } just Runs

        service.scrapeAndSaveOneLink(TEST_URL)

        verifySequence {
            eventScraper.loadDocumentFromUrl(TEST_URL)
            eventScraper.scrapeToEvent(doc, TEST_URL)
            calendarEventService.repository
            calendarEventRepository.save(event)
            issues.hasIssues()
            scrapeIssuesService.repository
            issues.url
            scrapeIssuesRepository.deleteById(TEST_URL)
        }
    }

    @Test
    fun `scrapeAndSaveOneLink had a scrape error`() {
        val TEST_URL = "http://test.example/"
        val doc = mockk<Document>()
        every { eventScraper.loadDocumentFromUrl(TEST_URL) } returns doc
        val issues = mockk<ScrapeIssues>()
        every { eventScraper.scrapeToEvent(doc, TEST_URL) } returns Pair(null, issues) // *** no event returned due to scrape issues
        every { issues.hasIssues() } returns true // *** there were issues with this scrape
        val scrapeIssuesRepository = mockk<ScrapeIssuesRepository>()
        every { scrapeIssuesService.repository } returns scrapeIssuesRepository
        every { scrapeIssuesRepository.save(issues) } returns issues

        service.scrapeAndSaveOneLink(TEST_URL)

        verifySequence {
            eventScraper.loadDocumentFromUrl(TEST_URL)
            eventScraper.scrapeToEvent(doc, TEST_URL)
            issues.hasIssues()
            scrapeIssuesService.repository
            scrapeIssuesRepository.save(issues)
        }
    }

    @Test
    fun getEventLinks() {
        val links = (1..1000).map { "entry%04d".format(it) }
        every { eventsFeed.extractLinksFromFeed(any()) } returns links
        val ret = service.getEventLinks()
        assertThat(ret).isEqualTo(links.take(config.maxEventsToScrape))
    }

    @Test
    @Disabled
    fun performFullSrapeAndSave() {
        TODO("this is hard to test -- it requires a lot of mocking")
    }
}