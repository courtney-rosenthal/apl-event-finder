package com.crosenthal.eventFinder.elasticsearch.domain

import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType

data class EventLocation(
    @Field(type = FieldType.Keyword)
    val key: String,

    @Field(type = FieldType.Text, store = true)
    val detail: String,
)