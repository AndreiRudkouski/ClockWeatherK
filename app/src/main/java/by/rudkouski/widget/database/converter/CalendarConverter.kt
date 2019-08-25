package by.rudkouski.widget.database.converter

import androidx.room.TypeConverter
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.ZonedDateTime
import java.util.*

class CalendarConverter {

    @TypeConverter
    fun fromCalendar(calendar: Calendar?): String? = if (calendar != null) DateTimeUtils.toZonedDateTime(calendar).toString() else null

    @TypeConverter
    fun toCalendar(value: String?): Calendar? = if (value != null) DateTimeUtils.toGregorianCalendar(ZonedDateTime.parse(value)) else null
}