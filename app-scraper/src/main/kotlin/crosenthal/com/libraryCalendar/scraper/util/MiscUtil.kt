package crosenthal.com.libraryCalendar.scraper.util

private val whitespace = "\\s+".toRegex()

fun String.cleanupWhiteSpace(): String {
    return this.trim().replace(whitespace, " ")
}

fun Throwable.getRootMessage(): String {
    var ex = this
    while (ex.cause != null) {
        ex = ex.cause!!
    }
    return ex.message ?: "${ex.javaClass.name} exception occurred"
}