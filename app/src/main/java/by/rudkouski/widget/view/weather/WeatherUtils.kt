package by.rudkouski.widget.view.weather

import android.content.Context
import by.rudkouski.widget.entity.DayForecast
import by.rudkouski.widget.entity.HourWeather
import by.rudkouski.widget.entity.Weather
import by.rudkouski.widget.entity.WeatherData
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonObject
import java.util.*

object WeatherUtils {

    private const val CURRENT_WEATHER = "currently"
    private const val HOUR_WEATHER = "hourly"
    private const val DAY_WEATHER = "daily"

    private val dateJsonDeserializer: JsonDeserializer<Date> =
        JsonDeserializer { json, _, _ ->
            Date(json.asJsonPrimitive.asLong * 1000)
        }
    private val gson = GsonBuilder().registerTypeAdapter(Date::class.java, dateJsonDeserializer).create()

    fun getWeatherFromResponseBody(responseBody: String): Weather {
        val jsonObject = gson.fromJson(responseBody, JsonObject::class.java).getAsJsonObject(CURRENT_WEATHER)
        return gson.fromJson(jsonObject, Weather::class.java)
    }

    fun getHourWeatherFromResponseBody(responseBody: String): HourWeather {
        val jsonObject = gson.fromJson(responseBody, JsonObject::class.java).getAsJsonObject(HOUR_WEATHER)
        return gson.fromJson(jsonObject, HourWeather::class.java)
    }

    fun getDayForecastFromResponseBody(responseBody: String): DayForecast {
        val jsonObject = gson.fromJson(responseBody, JsonObject::class.java).getAsJsonObject(DAY_WEATHER)
        return gson.fromJson(jsonObject, DayForecast::class.java)
    }

    fun getWeatherImageResource(context: Context, weather: WeatherData): Int {
        var cloudy = ""
        if (weather.iconName.startsWith("partly-cloudy")) {
            if (weather.cloudCover >= 0.6) cloudy = "_mostly"
            if (weather.cloudCover < 0.4) cloudy = "_less"
        }
        return context.resources.getIdentifier(weather.iconName.replace("-", "_") + cloudy, "mipmap",
            context.packageName)
    }
}