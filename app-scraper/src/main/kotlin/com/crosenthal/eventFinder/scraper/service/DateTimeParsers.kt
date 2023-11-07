package com.crosenthal.eventFinder.scraper.service

import com.crosenthal.eventFinder.elasticsearch.domain.EventDateTime
import com.crosenthal.eventFinder.elasticsearch.domain.RecommendedAge
import com.crosenthal.eventFinder.scraper.util.cleanupWhiteSpace
import org.springframework.stereotype.Service
import java.time.DateTimeException
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.DateTimeParseException
import java.util.Locale
import kotlin.text.RegexOption.CANON_EQ

@Service
class DateTimeParsers {

    internal val supportedLocales = listOf(
        Locale("en", "US"),
        Locale("es", "US"),
    )

    // "Wednesday, July 19, 2023"
    internal val supportedDatePatterns = listOf(
        "EEEE, MMMM d, yyyy",
    )

    internal val dateFormatters = supportedDatePatterns.flatMap { datePattern ->
        val formatter = DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern(datePattern)
            .toFormatter()
        supportedLocales.map { locale -> formatter.withLocale(locale) }
    }

    internal fun parseLocalDate(dateStr: String): LocalDate {
        var savedEx: Throwable? = null
        for (formatter in dateFormatters) {
            try {
                return LocalDate.parse(dateStr, formatter)
            } catch (ex: DateTimeParseException) {
                // ignore, try next locale
                savedEx = ex
            }
        }
        throw savedEx!!
    }


    // ======================================================================

    // "7:00 PM" or "12:00"
    internal val timeFormatters = listOf(
        DateTimeFormatter.ofPattern("h:m a"), // 12-hour with AM/PM
        DateTimeFormatter.ofPattern("k:m"),   // 24-hour
    )

    fun parseLocalTime(timeStr: String) : LocalTime {
        timeFormatters.forEach {
            try {
                return LocalTime.parse(timeStr, it)
            } catch (ex: DateTimeParseException) {
                // ignore
            }
        }
        throw DateTimeException("cannot parse time from: " + timeStr)
    }


    // ======================================================================

    internal fun MatchResult.verify(expected: Int) {
        val actual = this.groups.size - 1
        if (actual != expected) {
            throw IllegalArgumentException("expected ${expected} args, got ${actual} from: ${this.groups[0]}")
        }
    }
    internal fun MatchResult.getIntVal(idx: Int) = this.groups.get(idx)!!.value.toInt()

    internal val recommendedAgePatterns = listOf(

        "^All ages welcome$".toRegex(CANON_EQ) to { _ -> RecommendedAge(null, null) },

        "^Teens 13-18 only$".toRegex(CANON_EQ) to { _ -> RecommendedAge(13, 18) },

        "^Teens and adults welcome$".toRegex(CANON_EQ) to { _ -> RecommendedAge(13, null) },

        "^Recommended for ages (\\d+)-(\\d+)$".toRegex(CANON_EQ) to fun(matches: MatchResult) : RecommendedAge {
            matches.verify(2)
            return RecommendedAge(matches.getIntVal(1), matches.getIntVal(2))
        },

        "^Recommended for ages (\\d+) and up$".toRegex(CANON_EQ) to fun(matches: MatchResult) : RecommendedAge {
            matches.verify(1)
            return RecommendedAge(matches.getIntVal(1), null)
        },

        "^Recommended for ages (\\d+) and under$".toRegex(CANON_EQ) to fun(matches: MatchResult) : RecommendedAge {
            matches.verify(1)
            return RecommendedAge(null, matches.getIntVal(1))
        },

        "^Recommended for ages (\\d+) months to (\\d+) years$".toRegex(CANON_EQ) to fun(matches: MatchResult) : RecommendedAge {
            matches.verify(2)
            return RecommendedAge(Math.round(matches.getIntVal(1)/12.0).toInt(), matches.getIntVal(2))
        },

    )

    fun parseRecommendedAge(str: String) : RecommendedAge {
        val s1 = str.cleanupWhiteSpace()
        recommendedAgePatterns.forEach { (regex, generator ) ->
            val matches = regex.matchEntire(s1)
            if (matches != null) {
                return generator(matches)
            }
        }
        throw IllegalArgumentException("cannot extract recommended age from input")
    }


    // ======================================================================

    fun parseEventDateTime(str: String) : EventDateTime {
            // str => "Wednesday, July 19, 2023 - 7:00 PM to 8:00 PM"

            // dateStr => "Wednesday, July 19, 2023", timeRangeStr => "7:00 PM to 8:00 PM"
            val (dateStr, timeRangeStr) = let {
                val a = str.split(" - ")
                if (a.size != 2) {
                    throw IllegalArgumentException("failed to split date from time in \"$str\" (dropping field)")
                }
                a
            }
            val date = parseLocalDate(dateStr)

            // startTimeStr => "7:00 PM", endTimeStr => "8:00 PM"
            val (startTimeStr, endTimeStr) = let {
                val a = timeRangeStr.split(" to ")
                when (a.size) {
                    1 -> Pair(a[0], null)
                    2 -> Pair(a[0], a[1])
                    else -> {
                        throw IllegalArgumentException("failed to split start and end time in \"$timeRangeStr\"")
                    }
                }
            }
            val startTime = parseLocalTime(startTimeStr)
            val endTime = if (endTimeStr != null) parseLocalTime(endTimeStr) else null

            return EventDateTime.of(date, startTime, endTime)
        }

}