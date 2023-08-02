package com.crosenthal.eventFinder.elasticsearch.service

import com.crosenthal.eventFinder.elasticsearch.repository.ScrapeIssuesRepository


internal class ScrapeIssuesServiceTest {
    lateinit var repository: ScrapeIssuesRepository
    lateinit var service: ScrapeIssuesService

//    @BeforeEach
//    fun setup() {
//        repository = mockk<ScrapeIssuesRepository>()
//        service = ScrapeIssuesService(repository)
//    }
//
//    private fun makeIssues() : ScrapeIssues {
//        return ScrapeIssues(url = "http://example.com.document", eventSource = "<p>The quick brown fox.</p>")
//    }
//
//    // just a quick test to ensure the Base service is wired up ok
//    @Test
//    fun findById() {
//        val issues = makeIssues()
//        every { repository.findById(issues.url) } returns Optional.of(issues)
//        val result = service.findById(issues.url)
//        assertThat(result).isEqualTo(issues)
//    }


}