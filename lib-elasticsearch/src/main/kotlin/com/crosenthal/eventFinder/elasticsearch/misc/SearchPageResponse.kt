package com.crosenthal.eventFinder.elasticsearch.misc

import org.springframework.data.elasticsearch.core.SearchPage

data class SearchPageResponse<T>(
    val numResults: Long,
    val numPages: Int,
    val resultsPerPage: Int,
    val firstResult: Int?,
    val lastResult: Int?,
    val firstPage: Int,
    val lastPage: Int,
    val currPage: Int,
    val prevPage: Int?,
    val nextPage: Int?,
    val results: List<T>,
) {

    companion object {
        fun <T> build(results: SearchPage<T>): SearchPageResponse<T> {
            val numPages = results.totalPages
            val currPage = results.pageable.pageNumber
            val resultsPerPage = results.pageable.pageSize
            val firstResult = if (results.isEmpty) null else currPage * resultsPerPage
            val lastResult = if (results.isEmpty) null else firstResult!! + results.numberOfElements - 1
            return SearchPageResponse(
                numResults = results.totalElements,
                numPages = numPages,
                resultsPerPage = resultsPerPage,
                firstResult = firstResult,
                lastResult = lastResult,
                firstPage = 0,
                lastPage = (numPages-1).coerceAtLeast(0),
                currPage = currPage,
                prevPage = (currPage-1).let {
                    if (it < 0)
                        null
                    else
                        it
                },
                nextPage = (currPage+1).let {
                    if (it >= numPages)
                        null
                    else
                        it
                },
                results = results.searchHits.map {it.content}.toList()
            )
        }
    }

}
