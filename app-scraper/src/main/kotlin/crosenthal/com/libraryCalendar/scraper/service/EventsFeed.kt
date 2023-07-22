package crosenthal.com.libraryCalendar.scraper.service

import com.sun.syndication.feed.synd.SyndEntry
import com.sun.syndication.io.SyndFeedInput
import com.sun.syndication.io.XmlReader
import org.fissore.slf4j.FluentLoggerFactory
import org.springframework.stereotype.Service
import java.net.URL

@Service
class EventsFeed() {

    companion object {
        val LOG = FluentLoggerFactory.getLogger(EventsFeed::class.java)

    }


    /**
     * Extract the links from an RSS feed.
     */
    fun extractLinksFromFeed(source: URL): List<String> {
        LOG.info().log("extractLinksFromFeed: beginning scrape of source={}", source)
        val input = SyndFeedInput()
        val feed = input.build(XmlReader(source))
        val links = feed.entries.map { (it as SyndEntry).link }
        LOG.info().log("extractLinksFromFeed: finished, scraped {} links", links.size)
        return links
    }

}