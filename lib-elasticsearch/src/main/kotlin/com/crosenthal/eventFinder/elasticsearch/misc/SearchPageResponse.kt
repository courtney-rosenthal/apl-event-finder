package com.crosenthal.eventFinder.elasticsearch.misc

import org.springframework.data.elasticsearch.core.SearchPage

data class SearchPageResponse<T>(
    val numResults: Long,
    val numPages: Int,
    val resultsPerPage: Int,
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
            return SearchPageResponse(
                numResults = results.totalElements,
                numPages = numPages,
                resultsPerPage = results.pageable.pageSize,
                firstPage = 0,
                lastPage = (numPages-1).let {
                    if (it < 0)
                        0
                    else
                        it
                },
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
