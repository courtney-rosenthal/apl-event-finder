package crosenthal.com.eventFinder.scraper

import java.io.File
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

    fun streamTestDocumentsIndex() : Stream<String> {
        return ClassLoader.getSystemResourceAsStream(TEST_PAGES_INDEX).bufferedReader().lines()
    }

    fun listBadPages(): Stream<File> {
        val badPages = Thread.currentThread().getContextClassLoader().getResource("exampleContent/badPages/")!!
        return File(badPages.path).listFiles().filter { ! it.name.startsWith("README") }.stream()
    }

}