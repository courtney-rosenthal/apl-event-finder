package crosenthal.com.eventFinder.scraper.service

import com.crosenthal.eventFinder.elasticsearch.domain.CalendarEvent
import com.crosenthal.eventFinder.elasticsearch.domain.EventDateTime
import com.crosenthal.eventFinder.elasticsearch.domain.EventLocation
import com.crosenthal.eventFinder.elasticsearch.domain.RecommendedAge
import com.crosenthal.eventFinder.locations.LocationService
import com.crosenthal.eventFinder.scraper.service.DateTimeParsers
import com.crosenthal.eventFinder.scraper.service.EventScraper
import crosenthal.com.eventFinder.scraper.testHelpers.TEST_URL
import crosenthal.com.eventFinder.scraper.testHelpers.listBadPages
import crosenthal.com.eventFinder.scraper.testHelpers.openTestDocument
import crosenthal.com.eventFinder.scraper.testHelpers.streamTestDocumentsIndex
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

internal class EventScraperTest {

    private lateinit var scraper: EventScraper
    private lateinit var dateTimeParsers: DateTimeParsers
    private lateinit var locationService: LocationService

    @BeforeEach
    fun setup() {
        dateTimeParsers = DateTimeParsers()
        locationService = LocationService()
        locationService.initialize()
        scraper = EventScraper(dateTimeParsers, locationService)
    }

    @Test
    fun `process a single event`() {

        val expected =  CalendarEvent.Builder(
            url = TEST_URL,
            content = "-- ignored --"
        ).apply {
            title = "Graphic Novel Book Club"
            subTitle = "Batman and Robin"
            summary = "Read as much or as little as you'd like but be prepared for spoilers if you don't reach the end!"
            description = "<p>The Graphic Novel Book Club is an adult book club that meets monthly to discuss graphic novels and comics. Whether you’re new to the genre or are a long-time comic-lover you’re sure to find interesting conversation and a chance to read something new!</p>"
            recommendedAge = RecommendedAge(18, null)
            time = EventDateTime(
                start = Instant.parse("2023-07-20T00:00:00Z"),
                end = Instant.parse("2023-07-20T01:00:00Z"),
                localHourOfDay = 19,
                localDayOfWeek = "Wed",
            )
            location = EventLocation(key = null, detail = "Better Half Coffee\n406 Walsh St.")
            registrationUrl = "https://www.eventbrite.com/e/graphic-novel-book-club-batman-and-robin-by-grant-morrison-tickets-644454941077"
            isFree = true
            tags = setOf("Adult", "Graphic Novel Book Club")
        }.build(mockk())!!

        val doc = scraper.loadDocumentFromStream(openTestDocument(), TEST_URL)
        val (event, issues) = scraper.scrapeToEvent(doc, TEST_URL)
        assertThat(event).isNotNull
        assertThat(issues.hasIssues).isFalse()
        assertThat(event)
            .usingRecursiveComparison()
            .ignoringFields("content", "timestamp")
            .isEqualTo(expected)
        assertThat(event!!.content).startsWith("<div class=\"field-title\">")
    }


    @Test
    fun `process all events`() {
        streamTestDocumentsIndex().forEach { url ->
            val (event, issues) = let {
                val inStream = openTestDocument(url)
                val doc = scraper.loadDocumentFromStream(inStream, url)
                scraper.scrapeToEvent(doc, url)
            }
            assertThat(event).isNotNull
            assertThat(issues.hasIssues).isFalse()
            assertThat(event!!.url).isEqualTo(url)
        }
    }

    // These are documents that broke the parser.
    val problemDocuments = listOf(
        "talk-time-tuesdays-noon-7738279", // Text '12:00' could not be parsed at index 5
    )

    @Test
    fun `handle problem documents`() {
        listBadPages().forEach { file ->
            val (event, issues) = let {
                val filename = file.path
                val inStream = file.inputStream()
                val uri = file.toURI().toString()
                val doc = scraper.loadDocumentFromStream(inStream, uri)
                scraper.scrapeToEvent(doc, uri)
            }
            assertThat(issues.hasIssues).isFalse()
            assertThat(event).isNotNull
        }
    }


}