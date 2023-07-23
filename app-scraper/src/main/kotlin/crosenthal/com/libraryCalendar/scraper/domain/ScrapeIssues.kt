package crosenthal.com.libraryCalendar.scraper.domain

import org.jsoup.nodes.Node
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.Instant

@Document(indexName = "scrape-issues")
class ScrapeIssues(url: String, eventSource: String) {

    @Id
    val url = url

    @Field(type = FieldType.Text, store = true)
    val eventSource = eventSource

    @Field(type = FieldType.Nested)
    val issues = mutableListOf<ScrapeIssue>()

    @Field(type = FieldType.Date)
    val timestamp = Instant.now()

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