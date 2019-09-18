package by.rudkouski.widget.database.converter

import androidx.room.TypeConverter
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME

class OffsetDateTimeConverter {

    private val formatter = ISO_OFFSET_DATE_TIME

    @TypeConverter
    fun fromOffsetDateTime(date: OffsetDateTime?) = date?.format(formatter)

    @TypeConverter
    fun toOffsetDateTime(value: String?) = value?.let {
        return@let formatter.parse(value, OffsetDateTime::from)
    }
}