package com.dev.cameronc.androidutilities

import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.Years
import javax.inject.Inject

class DateFormatter @Inject constructor() {
    fun formatDateToLongFormat(date: String): String {
        val dateTime = LocalDate.parse(date)
        return dateTime.toString(DateFormats.dateFormat)
    }

    fun getTimeSpanFromNow(date: String): String {
        val localDate = DateTime.parse(date)

        return "Age ${Years.yearsBetween(localDate, DateTime.now()).years}"
    }

    fun getTimeSpanAtTime(beginDate: String?, endDate: String?): String = "Age ${Years.yearsBetween(DateTime.parse(beginDate), DateTime.parse(endDate)).years}"

    fun getYearSpan(startDate: String, endDate: String = ""): String {
        val startDateTime = DateTime.parse(startDate)
        val endDateTime = if (endDate.isEmpty()) DateTime.now() else DateTime.parse(endDate)

        return if (startDateTime.year() == endDateTime.year()) {
            "(${startDateTime.year().asString})"
        }
        else {
            "(${startDateTime.year().asString} – ${endDateTime.year().asString})"
        }
    }

    object DateFormats {
        const val dateFormat: String = "MMMM d, yyyy"
    }
}