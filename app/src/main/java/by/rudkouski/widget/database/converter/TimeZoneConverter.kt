package by.rudkouski.widget.database.converter

import androidx.room.TypeConverter
import java.util.*
import java.util.TimeZone.getDefault
import java.util.TimeZone.getTimeZone

class TimeZoneConverter {

    @TypeConverter
    fun fromTimeZone(timeZone: TimeZone?): String = timeZone?.id ?: getDefault().id

    @TypeConverter
    fun toTimeZone(name: String?): TimeZone = if (name.isNullOrEmpty()) getDefault() else getTimeZone(name)
}