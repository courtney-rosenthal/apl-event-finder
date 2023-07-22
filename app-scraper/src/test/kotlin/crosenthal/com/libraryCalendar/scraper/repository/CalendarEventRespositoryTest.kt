package crosenthal.com.libraryCalendar.scraper.repository

import crosenthal.com.libraryCalendar.scraper.service.EventScraper
import crosenthal.com.libraryCalendar.scraper.testHelpers.TEST_URL
import crosenthal.com.libraryCalendar.scraper.testHelpers.openTestDocument
import crosenthal.com.libraryCalendar.scraper.testHelpers.scrapeFromInputStream
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
        val state = scrapeFromInputStream(scraper, openTestDocument(), TEST_URL)
        calendarEventRepository.save(state.event)
    }

}