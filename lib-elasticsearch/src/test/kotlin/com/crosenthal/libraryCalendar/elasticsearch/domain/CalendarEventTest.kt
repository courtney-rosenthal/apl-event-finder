package com.crosenthal.libraryCalendar.elasticsearch.domain

import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.assertj.core.api.Assertions.assertThatNoException
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.reflect.KMutableProperty1

internal class CalendarEventTest {

    companion object {
        @JvmStatic
        fun valuesForCheckRequiredFields(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(CalendarEvent::title),
                Arguments.of(CalendarEvent::description),
                Arguments.of(CalendarEvent::time),
                Arguments.of(CalendarEvent::location),
            )
        }
    }

    @ParameterizedTest
    @MethodSource("valuesForCheckRequiredFields")
    fun checkRequiredFields(field: KMutableProperty1<CalendarEvent, Any?>) {

        // minimal event, with just the required fields set
        val event = CalendarEvent(
            url = "http://example.com/document.html",
            content = "<p>The quick brown fox.</p>",
            title = "title",
            description = "description",
            time = mockk(),
            location = "location",
        )

        // make sure event checks ok
        assertThatNoException()
            .describedAs("precondition: event checks ok")
            .isThrownBy { event.checkRequiredFields() }

        // set a required field to null and verify it fails the check
        var savedValue = field.get(event)
        field.set(event, null)

        assertThatIllegalStateException()
            .describedAs("expecting IllegalStateException when ${field.name} is null")
            .isThrownBy { event.checkRequiredFields() }

        // verify event returns to ok once the value is restored
        field.set(event, savedValue)

        assertThatNoException()
            .describedAs("postcondition: event checks ok after field is restored")
            .isThrownBy { event.checkRequiredFields() }

        assertThatNoException().isThrownBy {
            event.checkRequiredFields()
        }
    }

}