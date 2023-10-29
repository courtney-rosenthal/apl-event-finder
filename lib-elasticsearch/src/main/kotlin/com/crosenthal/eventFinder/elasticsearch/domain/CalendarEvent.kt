package com.crosenthal.eventFinder.elasticsearch.domain

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.Instant
import java.time.temporal.ChronoUnit

@Document(indexName = "#{@elasticsearchProperties.indexPrefix}events")
data class CalendarEvent(
    @Id
    val url: String,

    @Field(type = FieldType.Text, store = true)
    val content: String,

    @Field(type = FieldType.Boolean)
    val isDeleted: Boolean,

    @Field(type = FieldType.Text, store = true)
    val title: String,

    @Field(type = FieldType.Text, store = true)
    val subTitle: String?,

    @Field(type = FieldType.Text, store = true)
    val summary: String?,

    @Field(type = FieldType.Text, store = true)
    val description: String,

    @Field(type = FieldType.Object)
    val recommendedAge: RecommendedAge?,

    @Field(type = FieldType.Object)
    val time: EventDateTime,

    @Field(type = FieldType.Object)
    val location: EventLocation,

    @Field(type = FieldType.Keyword, ignoreAbove = 512)
    val registrationUrl: String?,

    @Field(type = FieldType.Boolean)
    val isFree: Boolean?,

    @Field(type = FieldType.Keyword)
    val tags: Set<String>,

    @Field(type = FieldType.Date)
    val timestamp: Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS),

) {

        class Builder(val url: String, val content: String) {
            var isDeleted: Boolean = false
            var title: String? = null
            var subTitle: String? = null
            var summary: String? = null
            var description: String? = null
            var recommendedAge: RecommendedAge? = null
            var time: EventDateTime? = null
            var location: EventLocation? = null
            var registrationUrl: String? = null
            var isFree: Boolean? = null
            var tags: Set<String>? = null

            fun build(issues: ScrapeIssues): CalendarEvent? {
                var ok = true
                if (title == null) {
                    issues.add("could not extract title for event", "event scrape failed")
                    ok = false
                }
                if (description == null) {
                    issues.add("could not extract description for event", "event scrape failed")
                    ok = false
                }
                if (time == null) {
                    issues.add("could not extract time for event", "event scrape failed")
                    ok = false
                }
                if (location == null) {
                    issues.add("could not extract location for event", "event scrape failed")
                    ok = false
                }
                return if (ok) {
                    CalendarEvent(
                        url = url,
                        content = content,
                        isDeleted = isDeleted,
                        title = title!!,
                        subTitle = subTitle,
                        summary = summary,
                        description = description!!,
                        recommendedAge = recommendedAge,
                        time = time!!,
                        location = location!!,
                        registrationUrl = registrationUrl,
                        isFree = isFree,
                        tags = tags ?: emptySet(),
                    )
                } else {
                    null
                }
            }

        } // class Builder
    }



