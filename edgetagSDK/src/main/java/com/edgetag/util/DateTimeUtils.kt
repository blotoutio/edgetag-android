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

    fun generateEventId(eventName:String):String{
        val time = Date().toString()
        val base64Eventname = android.util.Base64.encodeToString(eventName.toByteArray(),android.util.Base64.DEFAULT)
        val uuid = UUID.randomUUID()
        return "${base64Eventname}-${uuid}-${time}"
    }
}
