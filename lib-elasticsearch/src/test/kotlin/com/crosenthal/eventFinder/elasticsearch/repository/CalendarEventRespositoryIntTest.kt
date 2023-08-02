package com.crosenthal.eventFinder.elasticsearch.repository

import com.crosenthal.eventFinder.elasticsearch.TestUtil
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

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Test
    fun save() {
        val event0 = TestUtil.makeEvent()
        assertThat(repository.findById(event0.url)).isEmpty

        val event = repository.save(event0)
        assertThat(event)
            .usingRecursiveComparison()
            .ignoringFields("timestamp")
            .isEqualTo(event0)

        val result = repository.findById(event.url)
        assertThat(result.get()).usingRecursiveComparison().isEqualTo(event)
    }

}