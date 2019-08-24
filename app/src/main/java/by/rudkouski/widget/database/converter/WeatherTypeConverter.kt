package by.rudkouski.widget.database.converter

import androidx.room.TypeConverter
import by.rudkouski.widget.entity.Weather

class WeatherTypeConverter {

    @TypeConverter
    fun fromWeatherType(type: Weather.WeatherType?): String? = type?.name

    @TypeConverter
    fun toWeatherType(value: String?): Weather.WeatherType? = if (value != null) Weather.WeatherType.valueOf(value) else null
}