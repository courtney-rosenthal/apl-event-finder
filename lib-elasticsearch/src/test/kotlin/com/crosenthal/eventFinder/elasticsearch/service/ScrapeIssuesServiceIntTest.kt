package com.crosenthal.eventFinder.elasticsearch.service

import com.crosenthal.eventFinder.elasticsearch.ElasticsearchConfig
import com.crosenthal.eventFinder.elasticsearch.ElasticsearchProperties
import com.crosenthal.eventFinder.elasticsearch.domain.ScrapeIssues
import com.crosenthal.eventFinder.elasticsearch.repository.ScrapeIssuesRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import java.time.Duration
import java.time.Instant
import java.time.temporal.TemporalAmount


@SpringBootTest(classes = [ElasticsearchProperties::class, ElasticsearchConfig::class, ScrapeIssuesService::class])
@EnableConfigurationProperties(ElasticsearchProperties::class)
@Suppress("UNUSED_VARIABLE") // there's a bunch of unused variables, just ignore them
internal class ScrapeIssuesServiceIntTest {

    @Autowired @Qualifier("scrapeIssuesRepository")
    lateinit var repository: ScrapeIssuesRepository

    @Autowired @Qualifier("scrapeIssuesService")
    lateinit var service: ScrapeIssuesService

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    private fun makeIssues(url: String, timeAdjust: TemporalAmount) : ScrapeIssues {
        val issues = ScrapeIssues(url, eventSource = "", timestamp = Instant.now().plus(timeAdjust))
        return repository.save(issues)
    }

    @Test
    fun listIssues() {
        makeIssues("i50", Duration.ofMinutes(50))
        makeIssues("i20", Duration.ofMinutes(20))
        makeIssues("i10", Duration.ofMinutes(10))
        makeIssues("i30", Duration.ofMinutes(30))
        makeIssues("i40", Duration.ofMinutes(40))

        assertThat(service.listIssues()).extracting("url").containsExactly(
            "i50",
            "i40",
            "i30",
            "i20",
            "i10",
        )
    }

}