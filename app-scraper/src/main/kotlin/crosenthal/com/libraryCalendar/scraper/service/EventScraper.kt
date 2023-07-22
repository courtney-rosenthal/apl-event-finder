package crosenthal.com.libraryCalendar.scraper.service

import crosenthal.com.libraryCalendar.scraper.domain.CalendarEvent
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

    data class ScrapeIssue(
        val message: String,
        val resolution: String,
        val node: Node,
        val selector: String? = getBriefSelector(node),
    )

    class ScrapeState(url: String, eventSource: Element) {

        val event = CalendarEvent(url, eventSource.html())

        val issues = mutableListOf<ScrapeIssue>()

        val processedElements = mutableSetOf<String>()

    }


    fun Node.removeChildrenByTag(removeTags: List<String> = emptyList()): List<Node> {
        return this.childNodes().filterNot {
            it is Element && removeTags.contains(it.tagName())
        }
    }

    fun Node.gatherChildren(): List<TextNode> {
        return this.childNodes().gatherChildren()
    }

    fun List<Node>.gatherChildren() : List<TextNode> {
        return this.flatMap {
            when (it) {
                is Element -> it.gatherChildren()
                is TextNode -> listOf(it)
                else -> throw IllegalStateException("unexpected node type: ${it}")
            }
        }.filterNot { it.isBlank }
    }

    fun Node.singleTextNode(node: Node, state: ScrapeState): String? {
        return this.gatherChildren().singleTextNode(node, state)
    }

    fun List<TextNode>.singleTextNode(node: Node, state: ScrapeState) : String? {
        val n = this.size
        when (n) {
            0 -> {
                state.issues.add(ScrapeIssue("no text children found", "dropped", node))
                return null
            }
            1 -> {}
            else -> {
                state.issues.add(ScrapeIssue("has too many text children (expected 1, got $n", "ignored extra", node))
            }
        }
        return this.first().text()
    }


    // ======================================================================
    // field-specific scraping
    // ======================================================================

    internal fun extractLocation(element: Element, state: ScrapeState): String? {
        // TODO: fully implement location
        val txt = element.gatherChildren().map { it.text() }.joinToString("\n")
        return if (txt.isBlank()) {
            state.issues.add(ScrapeIssue("cannot extract location", "skipped", element))
            null
        } else {
            txt
        }
    }

    internal fun extractRegistration(element: Element, state: ScrapeState): String? {
        val links = element.select("a").toList()
        when (links.size) {
            0 -> {
                state.issues.add(ScrapeIssue("failed to find registration link", "skipped", element))
            }
            1 -> {}
            else -> {
                state.issues.add(ScrapeIssue("too manyregistration links foundk", "excess dropped", element))
            }
        }
        return links.firstOrNull()?.attr("href")
    }


    internal fun extractIsFree(element: Element, state: ScrapeState): Boolean? {
        val txt = element.singleTextNode(element, state)
        return when (txt) {
            null -> null
            "Free and open to the public | Gratis y abierto al público" -> true
            else -> {
                state.issues.add(ScrapeIssue("unknown event cost", "ignored", element))
                null
            }
        }
    }


    internal fun extractTags(element: Element, state: ScrapeState): List<String> {
        return element.childNodes()
            .filter { it is Element && it.tagName() == "a" }
            .map { it.singleTextNode(element, state) }
            .filterNotNull()
    }


    // ======================================================================
    // public scraping methods
    // ======================================================================


    fun loadDocumentFromUrl(url: String) : Document {
        return Jsoup.connect(url).get()
    }


    fun loadDocumentFromStream(inStream: InputStream, baseUri: String) : Document {
        return Jsoup.parse(inStream, "UTF-8", baseUri)
    }


    private val fieldHandlers = mapOf<String, (Element, ScrapeState) -> Unit>(

        "div.field-title" to { element: Element, state: ScrapeState ->
            state.event.title = element.singleTextNode(element, state)
        },

        "div.event_subtitle" to  { element: Element, state: ScrapeState ->
            state.event.subTitle = element.singleTextNode(element, state)
        },

        "div.apl-event-summary" to  { element: Element, state: ScrapeState ->
            state.event.summary = element.singleTextNode(element, state)
        },

        "div.apl-rec-age" to  { element: Element, state: ScrapeState ->
            val txt = element.singleTextNode(element, state)
            if (txt != null) {
                try {
                    state.event.recommendedAge = dateTimeParsers.parseRecommendedAge(txt)
                } catch (ex: IllegalArgumentException) {
                    state.issues.add(ScrapeIssue(ex.getRootMessage(), "dropped", element))
                }
            }
        },

        "div.field-event-time" to  { element: Element, state: ScrapeState ->
            val txt = element.removeChildrenByTag(listOf("i")).gatherChildren().singleTextNode(element, state)
            if (txt != null) {
                try {
                    state.event.time = dateTimeParsers.parseEventDateTime(txt)
                } catch (ex: IllegalArgumentException) {
                    state.issues.add(ScrapeIssue(ex.getRootMessage(), "dropped", element))
                }
            }
        },

        "div.field-event-loc" to  { element: Element, state: ScrapeState ->
            state.event.location = extractLocation(element, state)
        },

        "div.field-event-reg" to  { element: Element, state: ScrapeState ->
            state.event.registration = extractRegistration(element, state)
        },

        "div.apl-free" to  { element: Element, state: ScrapeState ->
            state.event.isFree = extractIsFree(element, state)
        },

        "div.apl-event-tags" to  { element: Element, state: ScrapeState ->
            state.event.tags = extractTags(element, state)
        },

    ) // end of fieldHandlers


    fun scrapeToEvent(doc: Document, url: String) : ScrapeState {

        val eventSource = let {
            val a = doc.select("div.apl-event")
            if (a.size != 1) {
                throw IllegalArgumentException("failed to locate event in page")
            }
            a.first()!!
        }

        val state = ScrapeState(url, eventSource)

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
            if (state.processedElements.contains(selector)) {
                state.issues.add(ScrapeIssue("encountered duplicate element", "dropped", node))
                continue
            }

            val handler = fieldHandlers[selector]
            if (handler == null) {
                // Don't have a handler for this node, so save it off for now.
                unusedNodes.add(node)
                continue
            }

            handler(node, state)
        }

        // Throw whatever was not consumed into the description.
        state.event.description = unusedNodes.map {it.outerHtml()}.joinToString("\n")

        // TODO - check for missing fields

        return state
    }

}