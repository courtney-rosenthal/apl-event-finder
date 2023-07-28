package com.crosenthal.eventFinder.elasticsearch.repository

import com.crosenthal.eventFinder.elasticsearch.domain.ScrapeIssues
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface ScrapeIssuesRepository : ElasticsearchRepository<ScrapeIssues, String>