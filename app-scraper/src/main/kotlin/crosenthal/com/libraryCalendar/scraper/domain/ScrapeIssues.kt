package crosenthal.com.libraryCalendar.scraper.domain

import org.jsoup.nodes.Node
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.Instant

@Document(indexName = "scrape-issues")
data class ScrapeIssues(

    @Id
    val url: String,

    @Field(type = FieldType.Text, store = true)
    val eventSource: String,

    @Field(type = FieldType.Nested)
    val issues: MutableList<ScrapeIssue> = mutableListOf(),

    @Field(type = FieldType.Date)
    val timestamp : Instant = Instant.now(),

) {

    @Field(type = FieldType.Boolean)
    var hasIssues: Boolean = false
        private set

    fun add(message: String, resolution: String, node: Node? = null) {
        issues.add(ScrapeIssue(message, resolution, node))
        hasIssues = true
    }
}

data class ScrapeIssue(
    val message: String,
    val resolution: String,
    val node: String? = null,
) {
    constructor(message: String, resolution: String, node: Node? = null) : this(message, resolution, node?.outerHtml())
}