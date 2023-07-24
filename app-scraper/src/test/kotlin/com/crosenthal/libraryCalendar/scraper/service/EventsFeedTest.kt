package crosenthal.com.libraryCalendar.scraper.service

import com.crosenthal.libraryCalendar.scraper.service.EventsFeed
import crosenthal.com.libraryCalendar.scraper.testHelpers.TEST_RSS_FEED
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


internal class EventsFeedTest {

    lateinit var eventsFeed: EventsFeed

    @BeforeEach
    fun setup() {
        eventsFeed = EventsFeed()
    }

    @Test
    fun extractLinksFromFeed() {
        val url = ClassLoader.getSystemResource(TEST_RSS_FEED)
        val feed = eventsFeed.extractLinksFromFeed(url)
        assertThat(feed).hasSize(272)
        assertThat(feed.first()).isEqualTo("http://library.austintexas.gov/event/graphic-novel-book-club/graphic-novel-book-club-7727946")
        assertThat(feed.last()).isEqualTo("http://library.austintexas.gov/event/youth-programs-workshops/college-prep-applying-texas-am-7728255")
    }

}