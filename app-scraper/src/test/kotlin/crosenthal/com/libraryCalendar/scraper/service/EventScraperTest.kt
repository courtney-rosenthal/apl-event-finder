package crosenthal.com.libraryCalendar.scraper.service

import crosenthal.com.libraryCalendar.scraper.domain.CalendarEvent
import crosenthal.com.libraryCalendar.scraper.domain.EventDateTime
import crosenthal.com.libraryCalendar.scraper.domain.RecommendedAge
import crosenthal.com.libraryCalendar.scraper.testHelpers.TEST_URL
import crosenthal.com.libraryCalendar.scraper.testHelpers.openTestDocument
import crosenthal.com.libraryCalendar.scraper.testHelpers.scrapeFromInputStream
import crosenthal.com.libraryCalendar.scraper.testHelpers.streamTestDocumentsIndex
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalTime
import java.time.Period

internal class EventScraperTest {

    private lateinit var scraper: EventScraper
    private lateinit var dateTimeParsers: DateTimeParsers

    @BeforeEach
    fun setup() {
        dateTimeParsers = DateTimeParsers()
        scraper = EventScraper(dateTimeParsers)
    }

    @Test
    fun `process a single event`() {
        val inStream = openTestDocument()
        val state = scrapeFromInputStream(scraper, inStream, TEST_URL)

        val expected =  CalendarEvent(
            url = TEST_URL,
            content = "-- ignored --"
        ).apply {
            title = "Graphic Novel Book Club"
            subTitle = "Batman and Robin"
            summary = "Read as much or as little as you'd like but be prepared for spoilers if you don't reach the end!"
            description = "<p>The Graphic Novel Book Club is an adult book club that meets monthly to discuss graphic novels and comics. Whether you’re new to the genre or are a long-time comic-lover you’re sure to find interesting conversation and a chance to read something new!</p>"
            recommendedAge = RecommendedAge(Period.ofYears(18), null)
            time = EventDateTime(
                LocalDate.parse("2023-07-19"),
                LocalTime.parse("19:00"),
                LocalTime.parse("20:00")
            )
            location = "place\nBetter Half Coffee\n406 Walsh St."
            registration = "https://www.eventbrite.com/e/graphic-novel-book-club-batman-and-robin-by-grant-morrison-tickets-644454941077"
            isFree = true
            tags = listOf("Adult", "Graphic Novel Book Club")
        }

        assertThat(state.event)
            .usingRecursiveComparison()
            .ignoringFields("content")
            .isEqualTo(expected)
        assertThat(state.event.content).startsWith("<div class=\"field-title\">")
    }


    @Test
    fun `process all events`() {
        streamTestDocumentsIndex().forEach { url ->
            val inStream = openTestDocument(url)
            val event = try {
                scrapeFromInputStream(scraper, inStream, url)
            } catch (ex: Throwable) {
                throw RuntimeException("error while loading: $url", ex)
            }
            assertThat(event).isNotNull()
        }
    }


}