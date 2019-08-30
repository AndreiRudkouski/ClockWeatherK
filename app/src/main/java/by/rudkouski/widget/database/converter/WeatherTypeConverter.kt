package by.rudkouski.widget.database.converter

import androidx.room.TypeConverter
import by.rudkouski.widget.entity.Weather

class WeatherTypeConverter {

    @TypeConverter
    fun fromWeatherType(type: Weather.Type?): String? = type?.name

    @TypeConverter
    fun toWeatherType(value: String?): Weather.Type? = if (value != null) Weather.Type.valueOf(value) else null
}