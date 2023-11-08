package com.crosenthal.eventFinder.elasticsearch.service

import com.crosenthal.eventFinder.elasticsearch.domain.ScrapeIssues
import com.crosenthal.eventFinder.elasticsearch.repository.ScrapeIssuesRepository
import org.elasticsearch.index.query.QueryBuilders
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder
import org.springframework.stereotype.Service

@Service
class ScrapeIssuesService(
    @Qualifier("scrapeIssuesRepository") repository: ScrapeIssuesRepository
) : BaseService<ScrapeIssues, ScrapeIssuesRepository>(repository) {

    @Autowired
    lateinit var operations: ElasticsearchOperations

    fun listIssues() : List<ScrapeIssues> {

        // TODO pagination

        var nativeSearchQuery = NativeSearchQueryBuilder()
            .withQuery(QueryBuilders.matchAllQuery())
            .withSort(Sort.by("timestamp").descending())
            .withPageable(Pageable.ofSize(20))
            .build()

        val hits = operations.search(nativeSearchQuery, ScrapeIssues::class.java)
        return hits.searchHits.map {it.content}
    }

}