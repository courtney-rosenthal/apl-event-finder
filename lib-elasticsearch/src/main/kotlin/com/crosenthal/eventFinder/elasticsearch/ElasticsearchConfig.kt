package com.crosenthal.eventFinder.elasticsearch

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.RestClients
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories


@Configuration
@EnableElasticsearchRepositories
open class ElasticsearchConfig(
    @Qualifier("elasticsearchProperties") val props: ElasticsearchProperties
) : AbstractElasticsearchConfiguration() {

    @Bean
    @Suppress("DEPRECATION") // https://github.com/spring-projects/spring-boot/issues/28598
    override fun elasticsearchClient(): org.elasticsearch.client.RestHighLevelClient {
        val clientconfiguration = ClientConfiguration.builder()
            .connectedTo(props.serverHostAndPort)
            .let {
                if (props.useSSL) {
                    it.usingSsl()
                } else {
                    it
                }
            }.let {
                val u = props.authUsername
                val p = props.authPassword
                if (u != null && p != null) {
                    it.withBasicAuth(u, p)
                } else {
                    it
                }
            }.build()
        return RestClients.create(clientconfiguration).rest()
    }

}