package com.crosenthal.eventFinder.locations

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LocationServiceTest {

    lateinit var service: LocationService

    @BeforeEach
    fun setup() {
        service = LocationService()
        service.initialize()
    }

    val ACE_KEY = "ACE"
    val ACE_NAME = "Central Library"


    @Test
    fun findLocationKey() {
        assertThat(service.findLocationKey("${ACE_NAME}, 3rd Floor")).isEqualTo(ACE_KEY)
        assertThat(service.findLocationKey("duh")).isNull()
    }

    @Test
    fun getAllLocations() {
        val a = service.getAllLocations()
        assertThat(a).hasSize(22)
        assertThat(a.entries.first().toPair()).isEqualTo(ACE_KEY to ACE_NAME)
    }
}