package com.crosenthal.libraryCalendar.elasticsearch.repository

import com.crosenthal.libraryCalendar.elasticsearch.ElasticsearchConfig
import com.crosenthal.libraryCalendar.elasticsearch.TestApplication
import com.crosenthal.libraryCalendar.elasticsearch.domain.CalendarEvent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest(classes = [ElasticsearchConfig::class])
class CalendarEventRespositoryTest {

    @Autowired
    lateinit var calendarEventRepository: CalendarEventRepository

    private val URL = "http://example.com/document.html"
    private val CONTENT = "<div>the quick brown fox</div>"

    @BeforeEach
    fun setup() {
        calendarEventRepository.deleteAll()
    }


    @Test
    fun save() {
        assertThat(calendarEventRepository.findById(URL)).isEmpty

        val event = CalendarEvent(url = URL, content = CONTENT)
        calendarEventRepository.save(event)

        val retrieved = calendarEventRepository.findById(URL)
        assertThat(retrieved).isNotEmpty
        assertThat(retrieved.get())
            .usingRecursiveComparison()
            .ignoringFields("timestamp")
            .isEqualTo(event)
    }

}