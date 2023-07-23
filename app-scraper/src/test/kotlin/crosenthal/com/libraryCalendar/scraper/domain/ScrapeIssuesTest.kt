package crosenthal.com.libraryCalendar.scraper.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ScrapeIssuesTest {

    @Test
    fun noIssuesFound() {
        val issues = ScrapeIssues("url", "eventSource")
        assertThat(issues.noIssuesFound).isTrue()
        issues.add("message", "resolution")
        assertThat(issues.noIssuesFound).isFalse()
    }

}