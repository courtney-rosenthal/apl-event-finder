package crosenthal.com.libraryCalendar.scraper.service

import crosenthal.com.libraryCalendar.scraper.domain.CalendarEvent
import crosenthal.com.libraryCalendar.scraper.domain.ScrapeIssues
import crosenthal.com.libraryCalendar.scraper.util.getRootMessage
import org.fissore.slf4j.FluentLoggerFactory
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.springframework.stereotype.Service
import java.io.InputStream
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

/**
 * Scrape a web page that contains a single event.
 */
@Service
class EventScraper(
    val dateTimeParsers: DateTimeParsers
) {

    companion object {
        val LOG = FluentLoggerFactory.getLogger(EventScraper::class.java)

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
        return if (this.size > 0) this.joinToString("\n") else null

    }

    fun Node.singleTextNode(node: Node, issues: ScrapeIssues, isOptional: Boolean = false): String? {
        return this.collectText().singleTextNode(node, issues, isOptional)
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

    internal fun extractLocation(element: Element, issues: ScrapeIssues): String? {
        // TODO: fully implement location
       return element.collectText().collate()
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


    internal fun extractTags(element: Element, issues: ScrapeIssues): List<String> {
        return element.childNodes()
            .filter { it is Element && it.tagName() == "a" }
            .map { it.singleTextNode(element, issues) }
            .filterNotNull()
    }


    // ======================================================================
    // public scraping methods
    // ======================================================================


    fun loadDocumentFromUrl(url: String) : Document {
        LOG.debug().log("loadDocumentFromUrl: entered, url = {}", url)
        val doc = Jsoup.connect(url).get()
        LOG.debug().log("loadDocumentFromUrl: finished")
        return doc
    }


    fun loadDocumentFromStream(inStream: InputStream, baseUri: String) : Document {
        LOG.debug().log("loadDocumentFromStream: entered, baseUri = {}", baseUri)
        val doc = Jsoup.parse(inStream, "UTF-8", baseUri)
        LOG.debug().log("loadDocumentFromUrl: finished")
        return doc
    }


    private val fieldHandlers = mapOf<String, (Element, CalendarEvent, ScrapeIssues) -> Unit>(

        "div.field-title" to { element: Element, event: CalendarEvent, issues: ScrapeIssues ->
            event.title = element.singleTextNode(element, issues)
        },

        "div.event_subtitle" to  { element: Element, event: CalendarEvent, issues: ScrapeIssues ->
            event.subTitle = element.singleTextNode(element, issues, isOptional = true)
        },

        "div.apl-event-summary" to  { element: Element, event: CalendarEvent, _: ScrapeIssues ->
            event.summary = element.collectText().collate()
        },

        "div.apl-rec-age" to  { element: Element, event: CalendarEvent, issues: ScrapeIssues ->
            val txt = element.singleTextNode(element, issues)
            if (txt != null) {
                try {
                    event.recommendedAge = dateTimeParsers.parseRecommendedAge(txt)
                } catch (ex: IllegalArgumentException) {
                    issues.add(ex.getRootMessage(), "dropped", element)
                }
            }
        },

        "div.field-event-time" to  { element: Element, event: CalendarEvent, issues: ScrapeIssues ->
            val txt = element.removeChildrenByTag(listOf("i")).collectText().singleTextNode(element, issues)
            if (txt != null) {
                try {
                    event.time = dateTimeParsers.parseEventDateTime(txt)
                } catch (ex: IllegalArgumentException) {
                    issues.add(ex.getRootMessage(), "dropped", element)
                }
            }
        },

        "div.field-event-loc" to  { element: Element, event: CalendarEvent, issues: ScrapeIssues ->
            event.location = extractLocation(element, issues)
        },

        "div.field-event-reg" to  { element: Element, event: CalendarEvent, issues: ScrapeIssues ->
            event.registrationUrl = extractRegistration(element, issues)
        },

        "div.apl-free" to  { element: Element, event: CalendarEvent, issues: ScrapeIssues ->
            event.isFree = extractIsFree(element, issues)
        },

        "div.apl-event-tags" to  { element: Element, event: CalendarEvent, issues: ScrapeIssues ->
            event.tags = extractTags(element, issues)
        },

    ) // end of fieldHandlers


    fun scrapeToEvent(doc: Document, url: String) : Pair<CalendarEvent, ScrapeIssues> {

        LOG.debug().log("scrapeToEvent: entered, url = {}", url)

        val eventSource = let {
            val a = doc.select("div.apl-event")
            if (a.size != 1) {
                throw IllegalArgumentException("failed to locate event in page")
            }
            a.first()!!
        }

        val event = CalendarEvent(url, eventSource.html())

        val issues = ScrapeIssues(url, eventSource.html())

        val processedElements = mutableSetOf<String>()

        val unusedNodes = mutableListOf<Node>()
        for (node in eventSource.childNodes()) {

            // skip empty text nodes
            if (node is TextNode && node.text().isBlank()) {
                continue
            }

            if (node !is Element) {
                throw IllegalStateException("don't know what to do with this node: " + node)
            }

            val selector = getBriefSelector(node)!!
            if (processedElements.contains(selector)) {
                issues.add("encountered duplicate element", "dropped", node)
                continue
            }

            val handler = fieldHandlers[selector]
            if (handler == null) {
                // Don't have a handler for this node, so save it off for now.
                unusedNodes.add(node)
                continue
            }

            handler(node, event, issues)
        }

        // Throw whatever was not consumed into the description.
        event.description = unusedNodes.map {it.outerHtml()}.joinToString("\n")

        try {
            event.checkRequiredFields()
        } catch (ex: IllegalStateException) {
            issues.add(ex.getRootMessage(), "ignored", null)
        }

        if (issues.hasIssues) {
            LOG.warn().log("scrapeToEvent: issues found with document url={}", url)

        }
        LOG.debug().log("scrapeToEvent: finished")
        return Pair(event, issues)
    }

}