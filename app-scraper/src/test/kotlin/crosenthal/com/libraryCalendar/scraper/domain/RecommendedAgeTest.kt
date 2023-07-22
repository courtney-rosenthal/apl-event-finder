package crosenthal.com.libraryCalendar.scraper.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Period


internal class RecommendedAgeTest {

    @Test
    fun recommendedAge() {
        val a = RecommendedAge(Period.ofYears(13), Period.ofYears(18))
        assertThat(a.toString()).isEqualTo("RecommendedAge(minAge=P13Y, maxAge=P18Y)")

        val b = RecommendedAge(null, Period.ofYears(18))
        assertThat(b.toString()).isEqualTo("RecommendedAge(minAge=null, maxAge=P18Y)")

        val c = RecommendedAge(Period.ofYears(13), null)
        assertThat(c.toString()).isEqualTo("RecommendedAge(minAge=P13Y, maxAge=null)")
    }

}