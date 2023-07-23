package crosenthal.com.libraryCalendar.scraper.repository

import crosenthal.com.libraryCalendar.scraper.domain.ScrapeIssues
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface ScrapeIssuesRepository : ElasticsearchRepository<ScrapeIssues, String>