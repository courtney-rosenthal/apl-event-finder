package com.crosenthal.eventFinder.elasticsearch.repository

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface CalendarEventRepository : ElasticsearchRepository<com.crosenthal.eventFinder.elasticsearch.domain.CalendarEvent, String>