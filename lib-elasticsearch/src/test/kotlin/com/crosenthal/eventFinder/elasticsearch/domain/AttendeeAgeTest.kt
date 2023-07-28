package com.crosenthal.eventFinder.elasticsearch.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class AttendeeAgeTest {

    @Test
    fun recommendedAge() {
        val a = RecommendedAge(13, 18)
        assertThat(a.toString()).isEqualTo("RecommendedAge(minYears=13, maxYears=18)")

        val b = RecommendedAge(null, 18)
        assertThat(b.toString()).isEqualTo("RecommendedAge(minYears=null, maxYears=18)")

        val c = RecommendedAge(13, null)
        assertThat(c.toString()).isEqualTo("RecommendedAge(minYears=13, maxYears=null)")
    }

}