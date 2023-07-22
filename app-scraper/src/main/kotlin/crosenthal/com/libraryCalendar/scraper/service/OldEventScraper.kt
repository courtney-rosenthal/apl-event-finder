package crosenthal.com.libraryCalendar.scraper.service

import crosenthal.com.libraryCalendar.scraper.domain.RecommendedAge
import crosenthal.com.libraryCalendar.scraper.domain.OldCalendarEvent
import crosenthal.com.libraryCalendar.scraper.domain.EventDateTime
import org.fissore.slf4j.FluentLoggerFactory
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.springframework.stereotype.Service
import org.w3c.dom.DOMException
import org.w3c.dom.DOMException.INDEX_SIZE_ERR
import org.w3c.dom.DOMException.INVALID_STATE_ERR
import java.io.InputStream
import java.lang.IllegalStateException

@Service
class OldEventScraper(
    val dateTimeParsers: DateTimeParsers
) {

    companion object {
        val LOG = FluentLoggerFactory.getLogger(OldEventScraper::class.java)
    }

    // ======================================================================
    // general scraping helpers
    // ======================================================================

    fun Element.getOneElement(selector: String): Element? {
        val elements = this.select(selector)
        if (elements.size != 1) {
            val a = elements.map { it.cssSelector().replace(".* > ".toRegex(), "") }
            LOG.warn().log("{}: expected selector={} to contain a single child node, got {} (dropping field), nodes = {}", this.baseUri(), selector, elements.size, a)
            return null
        }
        return elements.first()!!
    }


    internal fun Element.getTextContent(selector: String): String? {
        val element = this.getOneElement(selector)
        if (element == null) {
            return null
        }
        val child = element.childNode(0)
        if (child !is TextNode) {
            LOG.warn().log("{}: expected selector={} to contain a node, got {} (dropping field)", this.baseUri(), selector, child.javaClass)
            return null
        }
        return child.text()
    }

    // Walk the tree from a location and gather all of the text.
    internal fun Node.gatherText() : List<String> {
        if (this is TextNode) {
            return if (this.isBlank) emptyList() else listOf(this.text())
        }
        return this.childNodes().flatMap {
            it.gatherText()
        }
    }


    // ======================================================================
    // field-specific scraping
    // ======================================================================

    val eventSelectorPattern = "^.* > div.apl-event > ".toRegex()

    val expectedThings = setOf(
        "div.field-title",
        "div.event_subtitle",
        "div.apl-event-summary",

        // FIXME - the event description is here, not in a container -- let's try to change that
        "p",
        "ul",
        "p:nth-child(4)",
        "p:nth-child(5)",
        "p:nth-child(6)",
        "p:nth-child(7)",
        "p:nth-child(8)",
        "p:nth-child(9)",

        "div.apl-rec-age",
        "div.field-event-time",
        "div.field-event-loc",
        "div.field-event-reg",
        "div.apl-free",
        "div.apl-event-tags",
    )


    private fun checkEventStructure(eventSrc: Element) {
        for (node in eventSrc.childNodes()) {
            when (node) {
                is TextNode -> {} // do nothing
                is Element -> {
                    val a = node.cssSelector().replace(eventSelectorPattern, "")
                    if (! expectedThings.contains(a)) {
                        throw DOMException(INVALID_STATE_ERR, "unexpected item in event source: ${a}")
                    }
                    node.html()
                }
                else -> throw DOMException(INVALID_STATE_ERR, "unexpected content in event source: ${node}")
            }
        }
    }


    internal fun Element.extractSubTitle() : String? {
        val texts = this.getOneElement("div.event_subtitle")?.gatherText()
        if (texts == null) {
            return null
        }
        return when (texts.size) {
            0 -> return null
            1 -> return texts.first()
            else -> throw DOMException(INVALID_STATE_ERR, "multiple subtitle values found: ${texts}")
        }
    }


    internal fun Element.extractRecommendedAge() : RecommendedAge? {
        val element = this.getOneElement("div.apl-rec-age")
        return if (element == null) {
            null
        } else {
            dateTimeParsers.parseRecommendedAge(element.text())
        }
    }


    internal fun Element.extractTime() : EventDateTime? {
        // timestr => "Wednesday, July 19, 2023 - 7:00 PM to 8:00 PM"
        val timestr = this.getTextContent("div.field-event-time > div")
        if (timestr == null) {
            LOG.warn().log("{}: cannot extract event date/time (dropping field)", this.baseUri())
            return null
        }

        // dateStr => "Wednesday, July 19, 2023", timeRangeStr => "7:00 PM to 8:00 PM"
        val (dateStr, timeRangeStr) = let {
            val a = timestr.split(" - ")
            if (a.size != 2) {
                LOG.warn().log("{}: failed to split date from time in \"{}\" (dropping field)", this.baseUri(), timestr)
                return null
            }
            a
        }
        val date = dateTimeParsers.parseLocalDate(dateStr)

        val (startTimeStr, endTimeStr) = let {
            val a = timeRangeStr.split(" to ")
            when (a.size) {
                1 -> Pair(a[0], null)
                2 -> Pair(a[0], a[1])
                else -> {
                    LOG.warn().log("{}: failed to split start and end time in \"{}\" (dropping field)", this.baseUri(), timeRangeStr)
                    return null
                }
            }
        }
        // startTimeStr => "7:00 PM", endTimeStr => "8:00 PM"
        val startTime = dateTimeParsers.parseLocalTime(startTimeStr)
        val endTime = if (endTimeStr != null) dateTimeParsers.parseLocalTime(endTimeStr) else null

        return EventDateTime(date, startTime, endTime)
    }


    internal fun Element.extractLocation() : String? {
        val e = this.getOneElement("div.field-event-loc")
        if (e == null) {
            return null
        }
        // review external internal loc
        return e.gatherText().joinToString("\n")
    }


    internal fun Element.extractIsFree() : Boolean? {
        // at this time, every event is "free", so we'd expect this to always return true
        val txt = this.getTextContent("div.apl-free")
        return when (txt) {
            null -> {
                LOG.warn().log("{}: cannot extract free event info (dropping field)", this.baseUri())
                null
            }
            "Free and open to the public | Gratis y abierto al público" -> true
            else -> {
                LOG.warn().log("{}: unexpected free event info (dropping field): {}", this.baseUri(), txt)
                null
            }
        }
    }


    internal fun Element.extractRegistration() : String? {
        if (this.select("div.field-event-reg").isEmpty()) {
            return null
        }
        val link = this.select("div.field-event-reg > div > a")
        if (link.size != 1) {
            throw DOMException(INDEX_SIZE_ERR, "failed to find registration link")
        }
        if (link.text() != "Please register for this event.") {
            throw DOMException(INVALID_STATE_ERR, "unexpected text in registration link")
        }

        val href = link.attr("href")
        if (href.isEmpty()) {
            throw DOMException(INVALID_STATE_ERR, "failed to extract registration link")
        }
        return href
    }


    internal fun Element.extractTags() : List<String> {
        val a = this.getOneElement("div.apl-event-tags")
        return if (a == null) {
            emptyList()
        } else {
            a.select("a").map { it.text() }.filter {it.isNotBlank()}
        }
    }


    class Scrape(val url: String, val doc: Document) {

        val issues: MutableList<ScrapeIssue> = mutableListOf()

        data class ScrapeIssue(
            val selector: String,
            val problem: String,
            val detail: Map<String, Any> = emptyMap(),
            val method: Any = Thread.currentThread().stackTrace.first().methodName,
        )


        fun getBriefSelector(e: Element): String {
            return e.cssSelector().replace(".* > ".toRegex(), "")
        }

        fun Element.getOneElement(selector: String): Element? {
            val elements = this.select(selector)
            return when (elements.size) {
                0 -> {
                    issues.add(ScrapeIssue(
                        selector = selector,
                        problem = "matched no elements",
                    ))
                    null
                }
                1 -> {
                    elements.first()
                }
                else -> {
                    issues.add(ScrapeIssue(
                        selector = selector,
                        problem = "matched too many elements",
                        detail = mapOf("elements" to elements.map {getBriefSelector(it)})
                    ))
                    null
                }
            }
        }


        val eventSource = doc.body().getOneElement("div.apl-event")
                ?: throw IllegalStateException("failed to locate \"div.apl-event\" in document")

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


    fun scrapeToEvent(doc: Document, url: String) : OldCalendarEvent? {
        val eventSource = doc.getOneElement("div.apl-event")
        if (eventSource == null) {
            LOG.warn().log("{}: completlely failed to scrape event (dropping)")
            return null
        }

        var capture = false
        val description = Element("div")
        description.addClass("xxx-event-description")
        for (node in eventSource.childNodes()) {
            if (node is Element && node.cssSelector().endsWith("div.apl-event-summary")) {
                capture = true
                continue
            }
            if (node is Element && node.cssSelector().endsWith("div.apl-rec-age")) {
                break
            }
            if (capture) {
                description.appendChild(node)
            }
        }

        checkEventStructure(eventSource)

        return OldCalendarEvent(
            url = url,
            content = eventSource.html(),
            title = eventSource.getTextContent("div.field-title"),
            subTitle = eventSource.extractSubTitle(),
            summary = eventSource.getTextContent("div.apl-event-summary"),
            description = description.html(),
            recommendedAge = eventSource.extractRecommendedAge(),
            time = eventSource.extractTime(),
            location = eventSource.extractLocation(),
            isFree = eventSource.extractIsFree(),
            registration = eventSource.extractRegistration(),
            tags = eventSource.extractTags(),
        )
    }


}