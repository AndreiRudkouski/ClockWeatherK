package by.rudkouski.widget.database.converter

import androidx.room.TypeConverter
import java.util.*

class CalendarConverter {

    @TypeConverter
    fun fromCalendar(calendar: Calendar?): Long? = calendar?.timeInMillis

    @TypeConverter
    fun toCalendar(value: Long?): Calendar? = value?.let {
        GregorianCalendar().also { calendar ->
            calendar.timeInMillis = it
        }
    }
}