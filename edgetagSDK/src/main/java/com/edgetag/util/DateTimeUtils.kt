package com.edgetag.util

import java.util.*

class DateTimeUtils {

    fun getCurrentTimezoneOffsetInMin(): Int {
        return try {
            val tz = TimeZone.getDefault()
            val now = Date()
            tz.getOffset(now.time) / (1000 * 60)
        } catch (e: Exception) {
            0
        }
    }

    fun get13DigitNumberObjTimeStamp(): Long {
        val dateL = Date()
        return dateL.time
    }
}
