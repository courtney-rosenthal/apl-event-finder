package crosenthal.com.libraryCalendar.scraper.repository

import crosenthal.com.libraryCalendar.scraper.service.EventScraper
import crosenthal.com.libraryCalendar.scraper.testHelpers.TEST_URL
import crosenthal.com.libraryCalendar.scraper.testHelpers.openTestDocument
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
class CalendarEventRespositoryTest {

    @Autowired
    lateinit var calendarEventRepository: CalendarEventRepository

    @Autowired @Qualifier("eventScraper")
    lateinit var scraper: EventScraper

    @Test
    fun save() {
        val doc = scraper.loadDocumentFromStream(openTestDocument(), TEST_URL)
        val (event, issues) = scraper.scrapeToEvent(doc, TEST_URL)
        assertThat(issues.hasIssues).isTrue()
        calendarEventRepository.save(event)
    }

}