package com.crosenthal.eventFinder.elasticsearch.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ScrapeIssuesTest {

    @Test
    fun `adding issue`() {
        val MESSAGE = "message"
        val RESOLUTION = "resolution"

        val issues = ScrapeIssues("url", "eventSource")
        assertThat(issues.hasIssues()).isFalse()

        issues.add(MESSAGE, RESOLUTION)
        assertThat(issues.hasIssues()).isTrue()
        assertThat(issues.issues).hasSize(1)
        assertThat(issues.issues.first()).isEqualTo(ScrapeIssue(
            message = MESSAGE,
            resolution = RESOLUTION,
        ))
    }

}