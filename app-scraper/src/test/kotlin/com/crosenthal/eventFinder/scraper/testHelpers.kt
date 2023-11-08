package crosenthal.com.eventFinder.scraper

import java.io.File
import java.io.InputStream
import java.util.stream.Stream

object testHelpers {

    // this file contains a capture of the library events RSS feed
    const val TEST_RSS_FEED = "exampleContent/library.austintexas.gov_events-feed.xml"

    // this directory contains all of the pages referenced in that feed
    const val EXAMPLE_PAGES_DIR = "exampleContent/pages/"

    // this directory contains pages that presented problems to the parser
    const val BAD_PAGES_DIR = "exampleContent/badPages/"

    // this is an example URL of one piece of content in the TEST_PAGES directory
    const val TEST_URL = "http://library.austintexas.gov/event/graphic-novel-book-club/graphic-novel-book-club-7727946"

    fun openTestDocument(url: String = TEST_URL) : InputStream {
        File(TEST_URL)
        val filename = EXAMPLE_PAGES_DIR + url.split('/').last()
        val inStream = ClassLoader.getSystemResourceAsStream(filename)!!
        return inStream
    }

    internal fun streamPagesFrom(dir: String) : Stream<File> {
        val badPages = Thread.currentThread().getContextClassLoader().getResource(dir)!!
        return File(badPages.path).listFiles().filter { ! it.name.startsWith("README") }.stream()
    }

//    fun streamTestDocumentsIndex() : Stream<String> {
//        return ClassLoader.getSystemResourceAsStream(TEST_PAGES_INDEX).bufferedReader().lines()
//    }
//
//    fun listBadPages(): Stream<File> {
//        val badPages = Thread.currentThread().getContextClassLoader().getResource("exampleContent/badPages/")!!
//        return File(badPages.path).listFiles().filter { ! it.name.startsWith("README") }.stream()
//    }

}