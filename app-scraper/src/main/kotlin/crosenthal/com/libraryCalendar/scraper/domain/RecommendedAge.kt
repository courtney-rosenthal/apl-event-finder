package crosenthal.com.libraryCalendar.scraper.domain

import java.time.Period

data class RecommendedAge(
    val minAge: Period?,
    val maxAge: Period?
)
