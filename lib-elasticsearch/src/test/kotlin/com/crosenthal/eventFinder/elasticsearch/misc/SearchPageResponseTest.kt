package com.crosenthal.eventFinder.elasticsearch.misc

import com.crosenthal.eventFinder.elasticsearch.TestUtil.makeEvent
import com.crosenthal.eventFinder.elasticsearch.domain.CalendarEvent
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.core.*


internal class SearchPageResponseTest {

    @Test
    fun `test build response`() {
        val NUM_RESULTS = 26L
        val RESULTS_PER_PAGE = 5
        val NUM_PAGES = 6

        val searchHits = (0 .. (NUM_RESULTS-1)).map {
            SearchHit<CalendarEvent>(
                null,
                null,
                null,
                (999.9).toFloat(),
                null,
                null,
                null,
                null,
                null,
                null,
                makeEvent(url = "event%03d".format(it))
            )
        }

        val expectedResults = searchHits.map {it.content }

        val hits = SearchHitsImpl<CalendarEvent>(
            NUM_RESULTS,
            mockk<TotalHitsRelation>(),
            (999.9).toFloat(),
            null,
            searchHits,
            null,
            null
        )


        // first page
        with (Pageable.ofSize(RESULTS_PER_PAGE).withPage(0)) {
            val a = SearchPageResponse.build(SearchHitSupport.searchPageFor(hits, this))
            assertThat(a.numResults).isEqualTo(NUM_RESULTS)
            assertThat(a.numPages).isEqualTo(NUM_PAGES)
            assertThat(a.resultsPerPage).isEqualTo(RESULTS_PER_PAGE)
            assertThat(a.firstResult).isEqualTo(0)
//            assertThat(a.lastResult).isEqualTo(4)
            assertThat(a.firstPage).isEqualTo(0)
            assertThat(a.lastPage).isEqualTo(NUM_PAGES-1)
            assertThat(a.currPage).isEqualTo(0)
            assertThat(a.prevPage).isEqualTo(null)
            assertThat(a.nextPage).isEqualTo(1)
            assertThat(a.results).isEqualTo(expectedResults)
        }

        // second page
        with (Pageable.ofSize(RESULTS_PER_PAGE).withPage(1)) {
            val a = SearchPageResponse.build(SearchHitSupport.searchPageFor(hits, this))
            assertThat(a.numResults).isEqualTo(NUM_RESULTS)
            assertThat(a.numPages).isEqualTo(NUM_PAGES)
            assertThat(a.resultsPerPage).isEqualTo(RESULTS_PER_PAGE)
            assertThat(a.firstResult).isEqualTo(5)
//            assertThat(a.lastResult).isEqualTo(9)
            assertThat(a.firstPage).isEqualTo(0)
            assertThat(a.lastPage).isEqualTo(NUM_PAGES-1)
            assertThat(a.currPage).isEqualTo(1)
            assertThat(a.prevPage).isEqualTo(0)
            assertThat(a.nextPage).isEqualTo(2)
            assertThat(a.results).isEqualTo(expectedResults)
        }

        // FIXME - this test is broken
//        // last page
//        with (Pageable.ofSize(RESULTS_PER_PAGE).withPage(NUM_PAGES-1)) {
//            val a = SearchPageResponse.build(SearchHitSupport.searchPageFor(hits, this))
//            assertThat(a.numResults).isEqualTo(NUM_RESULTS)
//            assertThat(a.numPages).isEqualTo(NUM_PAGES)
//            assertThat(a.resultsPerPage).isEqualTo(RESULTS_PER_PAGE)
//            assertThat(a.firstResult).isEqualTo(9999)
//            assertThat(a.lastResult).isEqualTo(9999)
//            assertThat(a.firstPage).isEqualTo(0)
//            assertThat(a.lastPage).isEqualTo(NUM_PAGES-1)
//            assertThat(a.currPage).isEqualTo(NUM_PAGES-1)
//            assertThat(a.prevPage).isEqualTo(NUM_PAGES-2)
//            assertThat(a.nextPage).isEqualTo(null)
//            assertThat(a.results).isEqualTo(expectedResults)
//        }

    }

}