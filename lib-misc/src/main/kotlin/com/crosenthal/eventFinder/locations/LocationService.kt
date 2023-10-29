package com.crosenthal.eventFinder.locations

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

private data class Location(
    val key: String,
    val name: String
)

@Service
class LocationService {

    private lateinit var locations: List<Location>

    @PostConstruct
    fun initialize() {
        val mapper =
            YAMLMapper().registerKotlinModule()
        val istr = this.javaClass.classLoader.getResourceAsStream("locations.yml")
        locations = mapper.readValue(istr)
    }

    fun findLocationKey(detail: String) : String? {
        return locations.find { detail.startsWith(it.name) }?.key
    }

    fun getAllLocations() : Map<String, String> {
        return locations.map { it.key to it.name }.toMap()
    }

}