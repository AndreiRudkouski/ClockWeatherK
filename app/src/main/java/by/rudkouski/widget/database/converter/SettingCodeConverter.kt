package by.rudkouski.widget.database.converter

import androidx.room.TypeConverter
import by.rudkouski.widget.entity.Setting

class SettingCodeConverter {

    @TypeConverter
    fun fromSettingCode(code: Setting.Code?): String? = code?.name

    @TypeConverter
    fun toSettingCode(value: String?): Setting.Code? = if (value != null) Setting.Code.valueOf(value) else null
}