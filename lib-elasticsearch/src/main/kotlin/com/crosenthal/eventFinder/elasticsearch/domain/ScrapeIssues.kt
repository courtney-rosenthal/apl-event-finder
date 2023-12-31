package com.crosenthal.eventFinder.elasticsearch.domain

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.Instant
import java.time.temporal.ChronoUnit

@Document(indexName = "#{@elasticsearchProperties.indexPrefix}scrape-issues")
data class ScrapeIssues(

    @Id
    val url: String,

    @Field(type = FieldType.Text, store = true)
    val eventSource: String,

    @Field(type = FieldType.Nested)
    val issues: MutableList<ScrapeIssue> = mutableListOf(),

    @Field(type = FieldType.Date)
    val timestamp : Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS),

) {
    fun add(message: String, resolution: String, node: String? = null) {
        issues.add(ScrapeIssue(message, resolution, node))
    }

    fun hasIssues(): Boolean {
        return issues.isNotEmpty()
    }
}

data class ScrapeIssue(
    val message: String,
    val resolution: String,
    val node: String? = null,
)