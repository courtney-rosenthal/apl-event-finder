package com.crosenthal.libraryCalendar.elasticsearch.repository

import com.crosenthal.libraryCalendar.elasticsearch.ElasticsearchConfig
import com.crosenthal.libraryCalendar.elasticsearch.ElasticsearchProperties
import com.crosenthal.libraryCalendar.elasticsearch.domain.CalendarEvent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [ElasticsearchProperties::class, ElasticsearchConfig::class])
@EnableConfigurationProperties(ElasticsearchProperties::class)
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

        val event = repository.save(CalendarEvent(url = URL, content = CONTENT))

        val result = repository.findById(URL)
        assertThat(result).isNotEmpty
        assertThat(result.get()).isEqualTo(event)
    }

}