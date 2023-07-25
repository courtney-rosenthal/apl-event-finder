package com.crosenthal.libraryCalendar.elasticsearch.service

import com.crosenthal.libraryCalendar.elasticsearch.domain.ScrapeIssues
import com.crosenthal.libraryCalendar.elasticsearch.repository.ScrapeIssuesRepository
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Optional


internal class ScrapeIssuesServiceTest {
    lateinit var repository: ScrapeIssuesRepository
    lateinit var service: ScrapeIssuesService

    @BeforeEach
    fun setup() {
        repository = mockk<ScrapeIssuesRepository>()
        service = ScrapeIssuesService(repository)
    }

    private fun makeIssues() : ScrapeIssues {
        return ScrapeIssues(url = "http://example.com.document", eventSource = "<p>The quick brown fox.</p>")
    }

    // just a quick test to ensure the Base service is wired up ok
    @Test
    fun findById() {
        val issues = makeIssues()
        every { repository.findById(issues.url) } returns Optional.of(issues)
        val result = service.findById(issues.url)
        assertThat(result).isEqualTo(issues)
    }


}