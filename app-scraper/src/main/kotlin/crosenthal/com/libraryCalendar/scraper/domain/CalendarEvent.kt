package crosenthal.com.libraryCalendar.scraper.domain

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field


@Document(indexName = "events")
class CalendarEvent(url: String, content: String) {

    @Id
    val url = url

    @Field
    val content = content

    @Field
    var title: String? = null

    @Field
    var subTitle: String? = null

    @Field
    var summary: String? = null

    @Field
    var description: String? = null

    @Field
    var recommendedAge: RecommendedAge? = null

    @Field
    var time: EventDateTime? = null

    @Field
    var location: String? = null

    @Field
    var registration: String? = null

    @Field
    var isFree: Boolean? = null

    @Field
    var tags: List<String> = emptyList()

}

