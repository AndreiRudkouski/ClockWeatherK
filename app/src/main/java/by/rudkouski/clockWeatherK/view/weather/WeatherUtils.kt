package by.rudkouski.clockWeatherK.view.weather

import android.content.Context
import by.rudkouski.clockWeatherK.entity.Forecast
import by.rudkouski.clockWeatherK.entity.Weather
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonObject
import java.util.*

object WeatherUtils {

    private const val CURRENT_WEATHER = "currently"
    private const val DATE_FORMAT = "dd MMM yyyy"
    private const val FORECAST_DELIMITER = ",\"forecast\":"
    private const val DESCRIPTION_DELIMITER = ",\"description\""

    fun getWeatherFromResponseBody(responseBody: String): Weather {
        val dateJsonDeserializer: JsonDeserializer<Date> =
            JsonDeserializer { json, _, _ ->
                Date(json.asJsonPrimitive.asLong * 1000)
            }

        val gson = GsonBuilder().registerTypeAdapter(Date::class.java, dateJsonDeserializer).create()
        val jsonObject = gson.fromJson(responseBody, JsonObject::class.java).getAsJsonObject(CURRENT_WEATHER)
        return gson.fromJson(jsonObject, Weather::class.java)
    }

    fun getForecastsFromResponseBody(responseBody: String): List<Forecast> {
        val forecastsJson = parseResponseBodyToForecastJson(responseBody)
        val gson = GsonBuilder().setLenient().setDateFormat(DATE_FORMAT).create()
        return gson.fromJson(forecastsJson, Array<Forecast>::class.java).toList()
    }

    private fun parseResponseBodyToForecastJson(responseBody: String): String? {
        return responseBody.split(FORECAST_DELIMITER.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].split(
            DESCRIPTION_DELIMITER.toRegex()
        ).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
    }

    fun getWeatherImageResourceIdByName(context: Context, iconName: String): Int {
        return context.resources.getIdentifier(iconName.replace("-", "_"), "mipmap", context.packageName)
    }

    fun getWeatherImageResource(context: Context, weather: Weather): Int {
        var cloudy = ""
        if (weather.iconName.startsWith("party-cloudy")) {
            if (weather.cloudCover >= 0.5) cloudy = "_mostly"
            if (weather.cloudCover < 0.2) cloudy = "_less"
        }
        return context.resources.getIdentifier(weather.iconName.replace("-", "_") + cloudy, "mipmap",
            context.packageName)
    }
}