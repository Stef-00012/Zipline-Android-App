package com.stefdp.zipline.utils

import android.content.Context
import com.stefdp.zipline.R
import com.stefdp.zipline.network.models.Metric
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import kotlin.math.abs
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

fun formatMs(
    context: Context,
    ms: Double,
    abbreviated: Boolean = true,
    limit: Int = 7
): String {
    val duration = ms.toLong()

    val secondsShortSuffix = context.getString(R.string.seconds_short)
    val minutesShortSuffix = context.getString(R.string.minutes_short)
    val hoursShortSuffix = context.getString(R.string.hours_short)
    val daysShortSuffix = context.getString(R.string.days_short)
    val monthsShortSuffix = context.getString(R.string.months_short)
    val yearsShortSuffix = context.getString(R.string.years_short)

    if (duration == 0L) return "0$secondsShortSuffix"

    val second = 1.seconds.inWholeMilliseconds // 1000L
    val minute = 1.minutes.inWholeMilliseconds // second * 60
    val hour = 1.hours.inWholeMilliseconds // minute * 60
    val day = 1.days.inWholeMilliseconds // hour * 24
    val month = day * 30
    val year = day * 365

    val years = duration / year
    val months = (duration % year) / month
    val days = (duration % month) / day
    val hours = (duration % day) / hour
    val minutes = (duration % hour) / minute
    val seconds = (duration % minute) / second

    val secondsLongSuffix = context.resources.getQuantityString(R.plurals.seconds_unit, seconds.toInt()) //if (seconds > 1) "seconds" else "second"
    val minutesLongSuffix = context.resources.getQuantityString(R.plurals.minutes_unit, minutes.toInt()) //if (minutes > 1) "minutes" else "minute"
    val hoursLongSuffix = context.resources.getQuantityString(R.plurals.hours_unit, hours.toInt()) //if (hours > 1) "hours" else "hour"
    val daysLongSuffix = context.resources.getQuantityString(R.plurals.days_unit, days.toInt()) //if (days > 1) "days" else "day"
    val monthsLongSuffix = context.resources.getQuantityString(R.plurals.months_unit, months.toInt()) //if (months > 1) "months" else "month"
    val yearsLongSuffix = context.resources.getQuantityString(R.plurals.years_unit, years.toInt()) //if (years > 1) "years" else "year"

    val dateSegments = listOfNotNull(
        if (years > 0) "${years}${if (abbreviated) yearsShortSuffix else yearsLongSuffix}" else null,
        if (months > 0) "${months}${if (abbreviated) monthsShortSuffix else monthsLongSuffix}" else null,
        if (days > 0) "${days}${if (abbreviated) daysShortSuffix else daysLongSuffix}" else null,
        if (hours > 0) "${hours}${if (abbreviated) hoursShortSuffix else hoursLongSuffix}" else null,
        if (minutes > 0) "${minutes}${if (abbreviated) minutesShortSuffix else minutesLongSuffix}" else null,
        if (seconds > 0) "${seconds}${if (abbreviated) secondsShortSuffix else secondsLongSuffix}" else null
    )

    return dateSegments.take(limit).joinToString(" ")
}

private val yearUnit = listOf("years", "year", "yr", "y")
private val monthUnit = listOf("months", "month", "mo")
private val dayUnit = listOf("days", "day", "d")
private val hourUnit = listOf("hours", "hour", "hr", "h")
private val minuteUnit = listOf("minutes", "minute", "mins", "min", "m")
private val secondUnit = listOf("seconds", "second", "secs", "sec", "s")

private val combinedUnits = (yearUnit + monthUnit + dayUnit + hourUnit + minuteUnit + secondUnit).joinToString("|")
private val timeRegex = Regex("(\\d+)\\s*($combinedUnits)", RegexOption.IGNORE_CASE)

fun parseTimeToMillis(input: String): Long? {
    if (input.isBlank()) return null

    val matches = timeRegex.findAll(input.lowercase())

    if (!matches.any()) return null

    var totalMillis = 0L

    for (match in matches) {
        val value = match.groupValues[1].toLong()
        val unit = match.groupValues[2]

        totalMillis += when (unit) {
            in yearUnit -> value * 31_536_000_000L // 365 * 24 * 60 * 60 * 1000
            in monthUnit -> value * 2_592_000_000L // 30 * 24 * 60 * 60 * 1000
            in dayUnit -> value * 1.days.inWholeMilliseconds // 86_400_000L // 24 * 60 * 60 * 1000
            in hourUnit -> value * 1.hours.inWholeMilliseconds // 3_600_000L // 60 * 60 * 1000
            in minuteUnit -> value * 1.minutes.inWholeMilliseconds // 60_000L // 60 * 1000
            in secondUnit -> value * 1.seconds.inWholeMilliseconds // 1_000L
            else -> 0L
        }
    }

    if (totalMillis < 0) return null

    return totalMillis
}

fun formatDate(
    date: String,
    short: Boolean = false,
    dateOnly: Boolean = false,
    timeOnly: Boolean = false,
): String {
    if (dateOnly) {
        return Instant
            .parse(date)
            .atZone(ZoneOffset.systemDefault())
            .format(
                DateTimeFormatter.ofLocalizedDate(
                    if (short) FormatStyle.SHORT else FormatStyle.MEDIUM
                )
            )
    }

    if (timeOnly) {
        return Instant
            .parse(date)
            .atZone(ZoneOffset.systemDefault())
            .format(
                DateTimeFormatter.ofLocalizedTime(
                    if (short) FormatStyle.SHORT else FormatStyle.MEDIUM
                )
            )
    }

    return Instant
        .parse(date)
        .atZone(ZoneOffset.systemDefault())
        .format(
            DateTimeFormatter.ofLocalizedDateTime(
                if (short) FormatStyle.SHORT else FormatStyle.MEDIUM
            )
        )
}

fun isSameDate(isoString: String, instant: Instant): Boolean {
    val isoStringDate = Instant.parse(isoString).atZone(ZoneOffset.UTC).toLocalDate()

    val instantDate = instant.atZone(ZoneOffset.UTC).toLocalDate()

    return isoStringDate == instantDate
}

fun getDatesBetween(date1: LocalDate, date2: LocalDate): List<LocalDate> {
    val daysBetween = ChronoUnit.DAYS.between(date1, date2)

    return (0..daysBetween).map { date1.plusDays(it) }
}

fun getMetricDatesInBetween(
    startDate: String,
    endDate: String,
    stats: List<Metric>,
    numberOfPoints: Int
): List<Metric> {
    if (numberOfPoints <= 0 || stats.isEmpty()) return emptyList()

    val startMillis = Instant.parse(startDate).toEpochMilli()
    val endMillis = Instant.parse(endDate).toEpochMilli()

    val validStats = stats.filter {
        val time = Instant.parse(it.createdAt).toEpochMilli()
        time in startMillis..endMillis
    }

    if (validStats.isEmpty()) return emptyList()

    if (numberOfPoints == 1) {
        val midPoint = startMillis + (endMillis - startMillis) / 2
        return listOf(validStats.minByOrNull { abs(Instant.parse(it.createdAt).toEpochMilli() - midPoint) }!!)
    }

    val gap = (endMillis - startMillis).toDouble() / (numberOfPoints - 1)

    val targetTimestamps = (0 until numberOfPoints).map { index ->
        (startMillis + index * gap).toLong()
    }

    return targetTimestamps.mapNotNull { targetTime ->
        validStats.minByOrNull { metric ->
            val metricTime = Instant.parse(metric.createdAt).toEpochMilli()
            abs(metricTime - targetTime)
        }
    }.distinct()
}