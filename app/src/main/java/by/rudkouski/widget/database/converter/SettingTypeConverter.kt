package by.rudkouski.widget.database.converter

import androidx.room.TypeConverter
import by.rudkouski.widget.entity.Setting

class SettingTypeConverter {

    @TypeConverter
    fun fromSettingType(type: Setting.Type?): String? = type?.name

    @TypeConverter
    fun toSettingType(value: String?): Setting.Type? = if (value != null) Setting.Type.valueOf(value) else null
}