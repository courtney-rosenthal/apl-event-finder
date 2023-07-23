package crosenthal.com.libraryCalendar.scraper.domain

import org.jsoup.nodes.Node
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field

@Document(indexName = "scrape-issues")

class ScrapeIssues {

    @Field
    val issues: MutableList<ScrapeIssue> = mutableListOf()

    var noIssuesFound: Boolean = true
        private set

    fun add(message: String, resolution: String, node: Node? = null) {
        issues.add(ScrapeIssue(message, resolution, node))
        noIssuesFound = false
    }
}

data class ScrapeIssue(
    val message: String,
    val resolution: String,
    val node: String? = null,
) {
    constructor(message: String, resolution: String, node: Node? = null) : this(message, resolution, node?.outerHtml())
}