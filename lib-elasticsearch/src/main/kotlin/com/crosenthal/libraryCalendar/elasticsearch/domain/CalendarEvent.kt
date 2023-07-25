package com.crosenthal.libraryCalendar.elasticsearch.domain

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.Instant

@Document(indexName = "#{@elasticsearchProperties.indexPrefix}events")
data class CalendarEvent(

    @Id
    val url: String,

    @Field(type = FieldType.Text, store = true)
    val content: String,

    @Field(type = FieldType.Boolean)
    val isDeleted: Boolean = false,

    @Field(type = FieldType.Text, store = true)
    var title: String? = null,

    @Field(type = FieldType.Text, store = true)
    var subTitle: String? = null,

    @Field(type = FieldType.Text, store = true)
    var summary: String? = null,

    @Field(type = FieldType.Text, store = true)
    var description: String? = null,

    @Field(type = FieldType.Nested)
    var recommendedAge: RecommendedAge? = null,

    @Field(type = FieldType.Nested)
    var time: EventDateTime? = null,

    @Field(type = FieldType.Text, store = true)
    var location: String? = null,

    @Field(type = FieldType.Keyword, ignoreAbove = 512)
    var registrationUrl: String? = null,

    @Field(type = FieldType.Boolean)
    var isFree: Boolean? = null,

    @Field(type = FieldType.Keyword)
    var tags: List<String> = emptyList(),

    @Field(type = FieldType.Date)
    val timestamp: Instant = Instant.now(),

    ) {
    fun checkRequiredFields() {
        val missingFields: MutableList<String> = mutableListOf()
        if (title == null) {
            missingFields.add("title")
        }
        if (description == null) {
            missingFields.add("description")
        }
        if (time == null) {
            missingFields.add("time")
        }
        if (location == null) {
            missingFields.add("location")
        }
        if (missingFields.isNotEmpty()) {
            throw IllegalStateException("required field(s) missing: " + missingFields.joinToString(", "))
        }
    }
}

