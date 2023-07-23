package crosenthal.com.libraryCalendar.scraper.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.RestClients
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories


@Configuration
@EnableElasticsearchRepositories(basePackages = ["crosenthal.com.libraryCalendar.scraper.repository"])
class ElasticsearchConfig : AbstractElasticsearchConfiguration() {

    @Bean
    @Suppress("DEPRECATION") // https://github.com/spring-projects/spring-boot/issues/28598
    override fun elasticsearchClient(): org.elasticsearch.client.RestHighLevelClient {
        val clientConfiguration: ClientConfiguration = ClientConfiguration.builder()
            .connectedTo("localhost:9200")
            .build()
        return RestClients.create(clientConfiguration).rest()
    }

}