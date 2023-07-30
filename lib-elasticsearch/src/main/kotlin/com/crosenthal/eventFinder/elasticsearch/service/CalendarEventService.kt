package com.crosenthal.eventFinder.elasticsearch.service

import com.crosenthal.eventFinder.elasticsearch.domain.CalendarEvent
import com.crosenthal.eventFinder.elasticsearch.misc.CalendarEventSearchCriteria
import com.crosenthal.eventFinder.elasticsearch.misc.CalendarEventSearchCriteria.AttendeeAge
import com.crosenthal.eventFinder.elasticsearch.misc.CalendarEventSearchCriteria.Branch
import com.crosenthal.eventFinder.elasticsearch.misc.CalendarEventSearchCriteria.Day
import com.crosenthal.eventFinder.elasticsearch.misc.CalendarEventSearchCriteria.Time
import com.crosenthal.eventFinder.elasticsearch.repository.CalendarEventRepository
import org.elasticsearch.index.query.QueryBuilders
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
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
        days: Set<Day>? = null,
        times: Set<Time>? = null,
        branches: Set<Branch>? = null,
        age: AttendeeAge? = null,
        tags: Set<String>? = null,
        searchText: String? = null
    ): List<CalendarEvent> {
        return search(CalendarEventSearchCriteria(
            days = days,
            times = times,
            branches = branches,
            age = age,
            tags = tags,
            searchText = searchText
        ))
    }

    fun search(criteria: CalendarEventSearchCriteria): List<CalendarEvent> {

        // TODO: add tags
        // TODO: skip isDeleted == true
        // TODO: skip time.start < now

        var query = QueryBuilders.boolQuery()

        if (! criteria.days.isNullOrEmpty()) {
            val a = criteria.days.flatMap {it.expand()}.map {it.description }.toSet()
            query = query.must(QueryBuilders.termsQuery("time.localDayOfWeek", a))
        }

        if (! criteria.times.isNullOrEmpty()) {
            val a = criteria.times.flatMap {it.expand()}.toSet()
            query = query.must(QueryBuilders.termsQuery("time.localHourOfDay", a))
        }

        if (! criteria.branches.isNullOrEmpty()) {
            query = query.must(QueryBuilders.termsQuery("location.branch", criteria.branches.map(Branch::storedValue)))
        }

        if (criteria.age != null) {
            if (criteria.age.minYears != null) {
                val field_not_defined =
                    QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("recommendedAge.maxYears"))
                val fiend_in_range =
                    QueryBuilders.rangeQuery("recommendedAge.maxYears").gte(criteria.age.minYears)
                query = query.must(QueryBuilders.boolQuery()
                    .should(field_not_defined)
                    .should(fiend_in_range)
                    .minimumShouldMatch(1))
            }
            if (criteria.age.maxYears != null) {
                val field_not_defined =
                    QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("recommendedAge.minYears"))
                val fiend_in_range =
                    QueryBuilders.rangeQuery("recommendedAge.minYears").lte(criteria.age.maxYears)
                query = query.must(QueryBuilders.boolQuery()
                    .should(field_not_defined)
                    .should(fiend_in_range)
                    .minimumShouldMatch(1))
            }
        }

        if (! criteria.tags.isNullOrEmpty()) {
            query = query.must(QueryBuilders.termsQuery("tags", criteria.tags))
        }

        if (! criteria.searchText.isNullOrBlank()) {
            query = query.must(QueryBuilders.simpleQueryStringQuery(criteria.searchText))
        }

        // TODO: pagination

        var nativeSearchQuery = NativeSearchQueryBuilder()
            .withQuery(query)
            .withPageable(Pageable.ofSize(20))
            .build()

        val hits = operations.search(nativeSearchQuery, CalendarEvent::class.java)

        return hits.searchHits.map {it.content}
    }
}