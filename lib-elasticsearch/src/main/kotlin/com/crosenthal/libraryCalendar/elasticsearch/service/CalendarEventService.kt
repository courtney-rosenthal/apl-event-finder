package com.crosenthal.libraryCalendar.elasticsearch.service

import com.crosenthal.libraryCalendar.elasticsearch.domain.CalendarEvent
import com.crosenthal.libraryCalendar.elasticsearch.misc.SearchConditions
import com.crosenthal.libraryCalendar.elasticsearch.repository.CalendarEventRepository
import org.elasticsearch.index.query.QueryBuilders
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHits
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder
import org.springframework.stereotype.Service

@Service
class CalendarEventService(
    repository: CalendarEventRepository
) : BaseService<CalendarEvent, CalendarEventRepository>(repository) {

    @Autowired
    lateinit var operations: ElasticsearchOperations

    fun search(
        days: Set<SearchConditions.Day>?,
        times: Set<SearchConditions.Time>?,
        branches: Set<SearchConditions.Branch>?,
        q: String?
    ): SearchHits<CalendarEvent> {

        // TODO: add recommended age
        // TODO: add tags
        // TODO: skip isDeleted == true
        // TODO: skip time.start < now

        var query = QueryBuilders.boolQuery()

        if (! days.isNullOrEmpty()) {
            val a = days.flatMap {it.expand()}.map {it.name}.toSet()
            query = query.must(QueryBuilders.termsQuery("time.localDayOfWeek", a))
        }

        if (! times.isNullOrEmpty()) {
            val a = times.flatMap {it.expand()}.toSet()
            query = query.must(QueryBuilders.termsQuery("time.localHourOfDay", a))
        }

        if (! branches.isNullOrEmpty()) {
            query = query.must(QueryBuilders.termsQuery("location.branch", branches))
        }

        if (! q.isNullOrBlank()) {
            query = query.must(QueryBuilders.termQuery("content", q))
        }

        var nativeSearchQuery = NativeSearchQueryBuilder().withQuery(query).build()
        val hits = operations.search(nativeSearchQuery, CalendarEvent::class.java)
        return hits
    }
}