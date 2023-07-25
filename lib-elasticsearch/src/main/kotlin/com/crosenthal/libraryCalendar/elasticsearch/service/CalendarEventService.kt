package com.crosenthal.libraryCalendar.elasticsearch.service

import com.crosenthal.libraryCalendar.elasticsearch.domain.CalendarEvent
import com.crosenthal.libraryCalendar.elasticsearch.repository.CalendarEventRepository
import com.crosenthal.libraryCalendar.elasticsearch.repository.ScrapeIssuesRepository
import org.elasticsearch.index.query.QueryBuilders.matchQuery
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

    fun search(q: String): SearchHits<CalendarEvent> {
        var query = NativeSearchQueryBuilder()
            .withQuery(matchQuery("content", q))
            .build()

        val hits = operations.search(query, CalendarEvent::class.java)

        return hits
    }
}