package crosenthal.com.libraryCalendar.scraper.domain

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field


@Document(indexName = "events")
data class OldCalendarEvent(

    @Id
    val url: String,

    @Field
    val content: String,

    @Field
    val title: String?,

    @Field
    val subTitle: String?,

    @Field
    val summary: String?,

    @Field
    val description: String,

    @Field
    val recommendedAge: RecommendedAge?,

    @Field
    val time: EventDateTime?,

    @Field
    val location: String?,

    @Field
    val registration: String?,

    @Field
    val isFree: Boolean?,

    @Field
    val tags: List<String>,
)

