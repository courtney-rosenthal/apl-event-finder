package com.crosenthal.eventFinder.scraper.service

import com.crosenthal.eventFinder.elasticsearch.domain.CalendarEvent
import com.crosenthal.eventFinder.elasticsearch.domain.EventLocation
import com.crosenthal.eventFinder.elasticsearch.domain.ScrapeIssues
import com.crosenthal.eventFinder.locations.LocationService
import com.crosenthal.eventFinder.scraper.util.getRootMessage
import org.fissore.slf4j.FluentLoggerFactory
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.springframework.stereotype.Service
import java.io.InputStream

/**
 * Scrape a web page that contains a single event.
 */
@Service
class EventScraper(
    val dateTimeParsers: DateTimeParsers,
    val locationService: LocationService
) {

    companion object {
        val LOG = FluentLoggerFactory.getLogger(EventScraper::class.java)

        // Get the CSS selector string for a node and trim it to its final component
        private val selectorLeader = ".* > ".toRegex()
        fun getBriefSelector(n: Node) = if (n is Element) n.cssSelector().replace(selectorLeader, "") else null
    }

    // ======================================================================
    // general scraping helpers
    // ======================================================================


    fun Node.removeChildrenByTag(removeTags: List<String> = emptyList()): List<Node> {
        return this.childNodes().filterNot {
            it is Element && removeTags.contains(it.tagName())
        }
    }

    fun Node.collectText(): List<String> {
        return this.childNodes().collectText()
    }

    fun List<Node>.collectText(): List<String> {
        return this.flatMap {
            when (it) {
                is Element -> it.collectText()
                is TextNode -> listOf(it.text())
                else -> throw IllegalStateException("unexpected node type: ${it}")
            }
        }.map { it.trim() }.filter { it.isNotBlank() }
    }


    fun List<String>.collate(): String? {
        return if (this.size > 0) this.map { it.trim() }.joinToString("\n") else null
    }

    fun Node.singleTextNode(node: Node, issues: ScrapeIssues, isOptional: Boolean = false): String? {
        return this.collectText().singleTextNode(node, issues, isOptional)
    }

    fun ScrapeIssues.add(message: String, resolution: String, node: Node?) {
        this.add(message, resolution, node?.outerHtml())
    }

    fun List<String>.singleTextNode(node: Node, issues: ScrapeIssues, isOptional: Boolean = false) : String? {
        val n = this.size
        when (n) {
            0 -> {
                if (! isOptional) {
                    issues.add("no text children found", "dropped", node)
                }
                return null
            }
            1 -> {}
            else -> {
                issues.add("has too many text children (expected 1, got $n", "ignored extra", node)
            }
        }
        return this.first()
    }


    // ======================================================================
    // field-specific scraping
    // ======================================================================

    internal fun extractLocation(element: Element, issues: ScrapeIssues): EventLocation {
       val detail = element.collectText()
           .filter { it != "place"}     // filter out the event icon
           .collate()
           ?: let {
               issues.add("could not extract location", "continuing with unknown location", element.html())
               "(location unknown)"
           }
        val key = locationService.findLocationKey(detail)
        return EventLocation(key = key, detail = detail)
    }

    internal fun extractRegistration(element: Element, issues: ScrapeIssues): String? {
        val links = element.select("a").toList()
        when (links.size) {
            0 -> {
                issues.add("failed to find registration link", "skipped", element)
            }
            1 -> {}
            else -> {
                issues.add("too manyregistration links foundk", "excess dropped", element)
            }
        }
        return links.firstOrNull()?.attr("href")
    }


    internal fun extractIsFree(element: Element, issues: ScrapeIssues): Boolean? {
        val txt = element.singleTextNode(element, issues)
        return when (txt) {
            null -> null
            "Free and open to the public | Gratis y abierto al público" -> true
            else -> {
                issues.add("unknown event cost", "ignored", element)
                null
            }
        }
    }


    internal fun extractTags(element: Element, issues: ScrapeIssues): Set<String> {
        return element.childNodes()
            .filter { it is Element && it.tagName() == "a" }
            .map { it.singleTextNode(element, issues) }
            .filterNotNull()
            .toSet()
    }


    // ======================================================================
    // public scraping methods
    // ======================================================================


    fun loadDocumentFromUrl(url: String) : Document {
        LOG.debug().log("loadDocumentFromUrl: entered, url = {}", url)
        val doc = Jsoup.connect(url).get()
        return doc
    }


    fun loadDocumentFromStream(inStream: InputStream, baseUri: String) : Document {
        LOG.debug().log("loadDocumentFromStream: entered, baseUri = {}", baseUri)
        val doc = Jsoup.parse(inStream, "UTF-8", baseUri)
        return doc
    }


    private val fieldHandlers = mapOf<String, (Element, CalendarEvent.Builder, ScrapeIssues) -> Unit>(

        "div.field-title" to { element: Element, event: CalendarEvent.Builder, issues: ScrapeIssues ->
            event.title = element.singleTextNode(element, issues)
        },

        "div.event_subtitle" to  { element: Element, event: CalendarEvent.Builder, issues: ScrapeIssues ->
            event.subTitle = element.singleTextNode(element, issues, isOptional = true)
        },

        "div.apl-event-summary" to  { element: Element, event: CalendarEvent.Builder, _: ScrapeIssues ->
            event.summary = element.collectText().collate()
        },

        "div.apl-rec-age" to  { element: Element, event: CalendarEvent.Builder, issues: ScrapeIssues ->
            val txt = element.singleTextNode(element, issues)
            if (txt != null) {
                try {
                    event.recommendedAge = dateTimeParsers.parseRecommendedAge(txt)
                } catch (ex: IllegalArgumentException) {
                    issues.add(ex.getRootMessage(), "dropped", element)
                }
            }
        },

        "div.field-event-time" to  { element: Element, event: CalendarEvent.Builder, issues: ScrapeIssues ->
            val txt = element.removeChildrenByTag(listOf("i")).collectText().singleTextNode(element, issues)
            if (txt != null) {
                try {
                    event.time = dateTimeParsers.parseEventDateTime(txt)
                } catch (ex: IllegalArgumentException) {
                    issues.add(ex.getRootMessage(), "dropped", element)
                }
            }
        },

        "div.field-event-loc" to  { element: Element, event: CalendarEvent.Builder, issues: ScrapeIssues ->
            event.location = extractLocation(element, issues)
        },

        "div.field-event-reg" to  { element: Element, event: CalendarEvent.Builder, issues: ScrapeIssues ->
            event.registrationUrl = extractRegistration(element, issues)
        },

        "div.apl-free" to  { element: Element, event: CalendarEvent.Builder, issues: ScrapeIssues ->
            event.isFree = extractIsFree(element, issues)
        },

        "div.apl-event-tags" to  { element: Element, event: CalendarEvent.Builder, issues: ScrapeIssues ->
            event.tags = extractTags(element, issues)
        },

    ) // end of fieldHandlers


    fun scrapeToEvent(doc: Document, url: String) : Pair<CalendarEvent?, ScrapeIssues> {

        LOG.debug().log("scrapeToEvent: entered, url = {}", url)

        val eventSource = doc.select("div.apl-event").firstOrNull() ?: throw IllegalArgumentException("failed to locate \"apl-event\"")

        val eventBuilder = CalendarEvent.Builder(url, eventSource.html())

        val issues = ScrapeIssues(url, eventSource.html())

        val processedElements = mutableSetOf<String>()

        val unusedNodes = mutableListOf<Node>()
        for (node in eventSource.childNodes()) {

            // skip empty text nodes
            if (node is TextNode && node.text().isBlank()) {
                continue
            }

            // we expect that the direct children of the event Node will be Element (or empty TextNode, handled above)
            if (node !is Element) {
                issues.add("unsure what to do with this node", "dropped", node)
                continue
            }

            val selector = getBriefSelector(node)!!
            if (processedElements.contains(selector)) {
                issues.add("encountered duplicate element", "dropped", node)
                continue
            }

            val handler = fieldHandlers[selector]
            if (handler == null) {
                // Don't have a handler for this node, so assume it's event description. Save it off for later.
                unusedNodes.add(node)
                continue
            }

            handler(node, eventBuilder, issues)
        }

        // Throw whatever was not consumed into the description.
        eventBuilder.description = unusedNodes.map { it.outerHtml() }.joinToString("\n")

        val event =  eventBuilder.build(issues)
        if (issues.hasIssues) {
            LOG.warn().log("scrapeToEvent: issues found with document url={}", url)
        }
        return Pair(event, issues)
    }

}