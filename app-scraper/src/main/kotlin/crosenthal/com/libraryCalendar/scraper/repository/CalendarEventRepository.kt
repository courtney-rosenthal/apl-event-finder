package crosenthal.com.libraryCalendar.scraper.repository

import crosenthal.com.libraryCalendar.scraper.domain.CalendarEvent
import crosenthal.com.libraryCalendar.scraper.domain.OldCalendarEvent
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface CalendarEventRepository : ElasticsearchRepository<CalendarEvent, String>