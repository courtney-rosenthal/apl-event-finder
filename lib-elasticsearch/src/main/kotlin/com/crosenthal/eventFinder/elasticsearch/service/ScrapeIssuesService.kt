package com.crosenthal.eventFinder.elasticsearch.service

import com.crosenthal.eventFinder.elasticsearch.domain.ScrapeIssues
import com.crosenthal.eventFinder.elasticsearch.repository.ScrapeIssuesRepository
import org.springframework.stereotype.Service

@Service
class ScrapeIssuesService(
    repository: ScrapeIssuesRepository
) : BaseService<ScrapeIssues, ScrapeIssuesRepository>(repository)