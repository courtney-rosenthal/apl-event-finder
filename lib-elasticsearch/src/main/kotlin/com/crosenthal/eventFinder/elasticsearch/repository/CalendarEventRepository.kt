package com.crosenthal.eventFinder.elasticsearch.repository

import com.crosenthal.eventFinder.elasticsearch.domain.CalendarEvent
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface CalendarEventRepository : ElasticsearchRepository<CalendarEvent, String>