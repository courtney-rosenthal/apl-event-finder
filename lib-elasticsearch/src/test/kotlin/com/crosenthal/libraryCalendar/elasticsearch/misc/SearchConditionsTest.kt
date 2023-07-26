package com.crosenthal.libraryCalendar.elasticsearch.misc

import com.crosenthal.libraryCalendar.elasticsearch.misc.SearchConditions.Day
import com.crosenthal.libraryCalendar.elasticsearch.misc.SearchConditions.Time
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class SearchConditionsTest {

    @Test
    fun `expand Day`() {
        assertThat(Day.WEEKDAY.expand()).isEqualTo(setOf(Day.MON, Day.TUE, Day.WED, Day.THU, Day.FRI))
        assertThat(Day.WEEKEND.expand()).isEqualTo(setOf(Day.SAT, Day.SUN))
    }

    fun `expand Time`() {
        assertThat(Time.MORNING).isEqualTo(setOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11))
        assertThat(Time.AFTERNOON).isEqualTo(setOf(12, 13, 14, 15, 16))
        assertThat(Time.EVENING).isEqualTo(setOf(17, 18, 19, 20, 21, 22, 23))
    }

}