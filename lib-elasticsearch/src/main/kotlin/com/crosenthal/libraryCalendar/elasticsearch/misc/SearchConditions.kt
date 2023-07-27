package com.crosenthal.libraryCalendar.elasticsearch.misc

// These conditions are used in the CalendarEventService
object SearchConditions {

    interface IsDescribed {
        val description: String
    }

    enum class Day(override val description: String) : IsDescribed {
        SUN("Sun"),
        MON("Mon"),
        TUE("Tue"),
        WED("Wed"),
        THU("Thu"),
        FRI("Fri"),
        SAT("Sat"),
        WEEKDAY("weekday (Mon - Fri)"),
        WEEKEND("weekend (Sat, Sun)");

        fun expand(): Set<Day> {
            return when (this) {
                WEEKDAY -> setOf(MON, TUE, WED, THU, FRI)
                WEEKEND -> setOf(SAT, SUN)
                else -> setOf(this)
            }
        }

        fun storedValue(): String {
            return this.toString().lowercase().replaceFirstChar(Char::titlecase)
        }

    }

    enum class Time(override val description: String): IsDescribed {
        MORNING("morning (before noon)"),
        AFTERNOON("afternoon (between noon and 5PM)"),
        EVENING("evening (5pm or later)");

        @OptIn(ExperimentalStdlibApi::class)
        fun expand(): Set<Int> {
            return when (this) {
                MORNING -> (0 ..< 12).toSet()
                AFTERNOON -> (12 ..< 17).toSet()
                EVENING -> (17 ..< 24).toSet()
            }
        }
    }

    enum class Branch(override val description: String): IsDescribed {
        ACB("Carver"),
        ACE("Central"),
        ACP("Cepeda"),
        AOK("Hampton at Oak Hill"),
        AHC("History Center"),
        AHO("Howson"),
        ALW("Little Walnut"),
        AMR("Manchaca Road"),
        AMI("Milwood"),
        ANV("North Village"),
        AOQ("Old Quarry"),
        APH("Pleasant Hill"),
        ARR("Recycled Reads"),
        ARZ("Ruiz"),
        ASE("Southeast"),
        ASR("Spicewood Springs"),
        ASJ("St. John"),
        ATB("Terrazas"),
        ATO("Twin Oaks"),
        AUH("University Hills"),
        AWK("Willie Mae Kirk"),
        AWP("Windsor Park"),
        AYB("Yarborough"),
        OTHER("other location");

        fun storedValue(): String {
            return this.toString()
        }
    }

    // categories based on: https://www.cdc.gov/ncbddd/childdevelopment/positiveparenting/index.html
    enum class AttendeeAge(val minYears: Int?, val maxYears: Int?, override val description: String): IsDescribed {
        INFANT(null, 1, "Infant (1 year and below)"),
        TODDLER(1, 3, "Toddler (ages 1-3)"),
        PRESCHOOLER(3, 5, "Preschooler (ages 3-5)"),
        EARLY_CHILDHOOD(6, 8, "Early Childhood (ages 6-8)"),
        MIDDLE_CHILDHOOD(9, 11, "Middle Childhood (ages 9-11)"),
        YOUNG_TEEN(12, 14, "Young Teen (ages 12-14)"),
        TEEN(15, 18, "Teen (ages 15-17)"),
        ADULT(18, null, description = "Adult (ages 18 and up)");

    }

}