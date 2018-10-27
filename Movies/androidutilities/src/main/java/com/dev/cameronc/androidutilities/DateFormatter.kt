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

    object DateFormats {
        const val dateFormat: String = "MMMM d, yyyy"
    }
}