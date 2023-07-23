package crosenthal.com.libraryCalendar.scraper.repository

import crosenthal.com.libraryCalendar.scraper.domain.CalendarEvent
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface CalendarEventRepository : ElasticsearchRepository<CalendarEvent, String>