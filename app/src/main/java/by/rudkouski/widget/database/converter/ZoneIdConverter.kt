package by.rudkouski.widget.database.converter

import androidx.room.TypeConverter
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneId.of
import org.threeten.bp.ZoneId.systemDefault

class ZoneIdConverter {

    @TypeConverter
    fun fromZoneId(zoneId: ZoneId?): String = zoneId?.id ?: systemDefault().id

    @TypeConverter
    fun toZoneId(name: String?): ZoneId = if (name.isNullOrEmpty()) systemDefault() else of(name)
}