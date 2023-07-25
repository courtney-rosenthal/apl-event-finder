package com.crosenthal.libraryCalendar.elasticsearch.service

import com.crosenthal.libraryCalendar.elasticsearch.domain.CalendarEvent
import com.crosenthal.libraryCalendar.elasticsearch.repository.CalendarEventRepository
import com.crosenthal.libraryCalendar.elasticsearch.repository.ScrapeIssuesRepository
import org.elasticsearch.index.query.QueryBuilders.matchQuery
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder
import org.springframework.stereotype.Service

@Service
class CalendarEventService(
    repository: CalendarEventRepository
) : BaseService<CalendarEvent, CalendarEventRepository>(repository) {

    fun search(q: String?): List<CalendarEvent> {
        val query = NativeSearchQueryBuilder()
            .withQuery(matchQuery("content", q))
            .build()
        return emptyList()
    }
}