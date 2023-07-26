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
    }

}