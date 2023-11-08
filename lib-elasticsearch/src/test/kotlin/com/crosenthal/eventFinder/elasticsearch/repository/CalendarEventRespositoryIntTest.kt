package com.crosenthal.eventFinder.elasticsearch.repository

import com.crosenthal.eventFinder.elasticsearch.ElasticsearchConfig
import com.crosenthal.eventFinder.elasticsearch.ElasticsearchProperties
import com.crosenthal.eventFinder.elasticsearch.TestUtil
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [ElasticsearchProperties::class, ElasticsearchConfig::class])
@EnableConfigurationProperties(ElasticsearchProperties::class)
internal class CalendarEventRespositoryIntTest {

    @Autowired
    lateinit var repository: CalendarEventRepository

    @BeforeEach
    fun setup() {
        repository.deleteAll()
        assertThat(repository.findAll()).isEmpty()
    }


    @Test
    fun save() {
        val event0 = TestUtil.makeEvent()
        val event1 = repository.save(event0)
        assertThat(repository.count()).isEqualTo(1)
        assertThat(event1)
            .usingRecursiveComparison()
            .isEqualTo(event0)
    }

    @Test
    fun findbyId() {
        val event0 = TestUtil.makeEvent()
        repository.save(event0)
        assertThat(repository.count()).isEqualTo(1)
        val event1 = repository.findById(event0.url).orElse(null)
        assertThat(event1)
            .usingRecursiveComparison()
            .isEqualTo(event0)
    }

    @Test
    fun update() {
        val event0 = TestUtil.makeEvent()
        repository.save(event0)
        assertThat(repository.count()).isEqualTo(1)
        val UPDATED = "I am updated content"
        val event1 = event0.copy(content = UPDATED)
        repository.save(event1)
        assertThat(repository.count()).isEqualTo(1)
        val event2 = repository.findAll().first()
        assertThat(event2).usingRecursiveComparison().isEqualTo(event1)
    }

}