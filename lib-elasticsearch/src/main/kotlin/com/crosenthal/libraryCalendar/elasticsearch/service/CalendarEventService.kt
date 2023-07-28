package com.crosenthal.libraryCalendar.elasticsearch.service

import com.crosenthal.libraryCalendar.elasticsearch.domain.CalendarEvent
import com.crosenthal.libraryCalendar.elasticsearch.misc.SearchConditions
import com.crosenthal.libraryCalendar.elasticsearch.repository.CalendarEventRepository
import org.elasticsearch.index.query.QueryBuilders
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder
import org.springframework.stereotype.Service

@Service
class CalendarEventService(
    repository: CalendarEventRepository
) : BaseService<CalendarEvent, CalendarEventRepository>(repository) {

    @Autowired
    lateinit var operations: ElasticsearchOperations

    fun search(
        days: Set<SearchConditions.Day>? = null,
        times: Set<SearchConditions.Time>? = null,
        branches: Set<SearchConditions.Branch>? = null,
        age: SearchConditions.AttendeeAge? = null,
        q: String? = null
    ): List<CalendarEvent> {

        // TODO: add tags
        // TODO: skip isDeleted == true
        // TODO: skip time.start < now

        var query = QueryBuilders.boolQuery()

        if (! days.isNullOrEmpty()) {
            val a = days.flatMap {it.expand()}.map {it.description }.toSet()
            query = query.must(QueryBuilders.termsQuery("time.localDayOfWeek", a))
        }

        if (! times.isNullOrEmpty()) {
            val a = times.flatMap {it.expand()}.toSet()
            query = query.must(QueryBuilders.termsQuery("time.localHourOfDay", a))
        }

        if (! branches.isNullOrEmpty()) {
            query = query.must(QueryBuilders.termsQuery("location.branch", branches.map(SearchConditions.Branch::storedValue)))
        }

        if (age != null) {
            if (age.minYears != null) {
                val field_not_defined =
                    QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("recommendedAge.maxYears"))
                val fiend_in_range =
                    QueryBuilders.rangeQuery("recommendedAge.maxYears").gte(age.minYears)
                query = query.must(QueryBuilders.boolQuery()
                    .should(field_not_defined)
                    .should(fiend_in_range)
                    .minimumShouldMatch(1))
            }
            if (age.maxYears != null) {
                val field_not_defined =
                    QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("recommendedAge.minYears"))
                val fiend_in_range =
                    QueryBuilders.rangeQuery("recommendedAge.minYears").lte(age.maxYears)
                query = query.must(QueryBuilders.boolQuery()
                    .should(field_not_defined)
                    .should(fiend_in_range)
                    .minimumShouldMatch(1))
            }
        }

        if (! q.isNullOrBlank()) {
            query = query.must(QueryBuilders.simpleQueryStringQuery(q))
        }

        var nativeSearchQuery = NativeSearchQueryBuilder().withQuery(query).build()
        val hits = operations.search(nativeSearchQuery, CalendarEvent::class.java)

        return hits.searchHits.map {it.content}
    }
}