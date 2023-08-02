package com.crosenthal.eventFinder.elasticsearch.service

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

abstract class BaseService<D, R: ElasticsearchRepository<D, String>>(val repository: R)