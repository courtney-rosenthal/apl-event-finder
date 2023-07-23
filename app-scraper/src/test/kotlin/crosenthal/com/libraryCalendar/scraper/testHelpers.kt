package crosenthal.com.libraryCalendar.scraper

import crosenthal.com.libraryCalendar.scraper.domain.CalendarEvent
import crosenthal.com.libraryCalendar.scraper.service.EventScraper
import org.junit.platform.commons.util.Preconditions
import java.io.InputStream
import java.util.stream.Stream

object testHelpers {

    // this file contains a capture of the library events RSS feed
    const val TEST_RSS_FEED = "exampleContent/library.austintexas.gov_events-feed.xml"

    // this directory contains all of the pages referenced in that feed
    // note that ".html" has been added to the pages so they can be browsed on disk
    const val TEST_PAGES = "exampleContent/pages/"

    // this file contains a list of all the content in the TEST_PAGES directory
    const val TEST_PAGES_INDEX = TEST_PAGES + "/index"

    // this is an example URL of one piece of content in the TEST_PAGES directory
    const val TEST_URL = "http://library.austintexas.gov/event/graphic-novel-book-club/graphic-novel-book-club-7727946"

    fun openTestDocument(url: String = TEST_URL) : InputStream {
        val filename = TEST_PAGES + url.split('/').last()
        val inStream = ClassLoader.getSystemResourceAsStream(filename)!!
        return inStream
    }

//    fun scrapeFromInputStream(scraper: EventScraper, inStream: InputStream, url: String) : Pair<CalendarEvent, ScrapeIssues> {
//        val doc = scraper.loadDocumentFromStream(inStream, url)
//        val result = scraper.scrapeToEvent(doc, url)
//        return result
//    }

    fun streamTestDocumentsIndex() : Stream<String> {
        return ClassLoader.getSystemResourceAsStream(TEST_PAGES_INDEX).bufferedReader().lines()
    }

}