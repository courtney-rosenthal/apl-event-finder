package com.crosenthal.eventFinder.elasticsearch.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [com.crosenthal.eventFinder.elasticsearch.ElasticsearchProperties::class, com.crosenthal.eventFinder.elasticsearch.ElasticsearchConfig::class])
@EnableConfigurationProperties(com.crosenthal.eventFinder.elasticsearch.ElasticsearchProperties::class)
class CalendarEventRespositoryIntTest {

    @Autowired
    lateinit var repository: CalendarEventRepository

    private val URL = "http://example.com/document.html"
    private val CONTENT = "<div>the quick brown fox</div>"

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Test
    fun save() {
        assertThat(repository.findById(URL)).isEmpty

        val event = repository.save(
            com.crosenthal.eventFinder.elasticsearch.domain.CalendarEvent(
                url = URL,
                content = CONTENT
            )
        )

        val result = repository.findById(URL)
        assertThat(result).isNotEmpty
        assertThat(result.get()).isEqualTo(event)
    }

}