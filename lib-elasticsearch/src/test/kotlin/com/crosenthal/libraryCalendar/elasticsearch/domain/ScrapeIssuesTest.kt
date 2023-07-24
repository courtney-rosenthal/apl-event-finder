package com.crosenthal.libraryCalendar.elasticsearch.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ScrapeIssuesTest {

    @Test
    fun noIssuesFound() {
        val issues = ScrapeIssues("url", "eventSource")
        assertThat(issues.hasIssues).isFalse()
        issues.add("message", "resolution")
        assertThat(issues.hasIssues).isTrue()
    }

}