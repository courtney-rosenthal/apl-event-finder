package com.crosenthal.libraryCalendar.elasticsearch.service

import com.crosenthal.libraryCalendar.elasticsearch.domain.ScrapeIssues
import com.crosenthal.libraryCalendar.elasticsearch.repository.ScrapeIssuesRepository
import org.springframework.stereotype.Service

@Service
class ScrapeIssuesService(
    repository: ScrapeIssuesRepository
) : BaseService<ScrapeIssues, ScrapeIssuesRepository>(repository)