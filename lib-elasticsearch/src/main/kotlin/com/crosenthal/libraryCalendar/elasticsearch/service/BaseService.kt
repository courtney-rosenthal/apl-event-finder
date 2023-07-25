package com.crosenthal.libraryCalendar.elasticsearch.service

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

abstract class BaseService<D, R: ElasticsearchRepository<D, String>>(val repository: R) {
    fun save(entity: D): D {
        return repository.save(entity)
    }

    fun findById(id: String): D? {
        return repository.findById(id).orElse(null)
    }
}