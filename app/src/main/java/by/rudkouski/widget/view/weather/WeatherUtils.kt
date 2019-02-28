package by.rudkouski.widget.view.weather

import android.content.Context
import android.text.Spannable
import android.view.View
import android.widget.TextView
import by.rudkouski.widget.R
import by.rudkouski.widget.app.App.Companion.appContext
import by.rudkouski.widget.entity.DayForecast
import by.rudkouski.widget.entity.HourWeather
import by.rudkouski.widget.entity.Weather
import by.rudkouski.widget.entity.WeatherData
import com.google.gson.*
import java.lang.reflect.Type
import java.util.*

object WeatherUtils {

    private const val CURRENT_WEATHER = "currently"
    private const val HOUR_WEATHER = "hourly"
    private const val DAY_WEATHER = "daily"

    private const val WEATHER_DEGREE_FORMAT = "%1\$d%2\$s"
    private const val DETERMINATION_PATTERN = "%1\$s: %2\$s"

    private val dateJsonDeserializer: JsonDeserializer<Calendar> = object : JsonDeserializer<Calendar> {
        override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): Calendar {
            val date = Calendar.getInstance()
            date.time = Date(json.asJsonPrimitive.asLong * 1000)
            return date
        }
    }

    private val gson = GsonBuilder().registerTypeAdapter(Calendar::class.java, dateJsonDeserializer).create()

    fun getCurrentTimeZoneNameFromResponseBody(responseBody: String): String {
        val jsonObject = gson.fromJson(responseBody, JsonObject::class.java)
        return jsonObject.get("timezone").asString
    }

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

    fun convertDoubleToPercents(double: Double) =
        "${mathRound(double * 100)}${appContext.getString(R.string.percent_unit)}"

    fun getDegreeText(temperature: Double) =
        String.format(Locale.getDefault(), WEATHER_DEGREE_FORMAT, mathRound(temperature),
            appContext.getString(R.string.temperature_unit))

    fun mathRound(double: Double) = Math.round(double)

    fun setDataToView(view: View, identifier: Int, description: String?, value: String?) {
        val textView = view.findViewById<TextView>(identifier)
        textView.text = if (description != null) convertToDeterminationPattern(description, value!!) else value
    }

    fun setDataToView(view: View, identifier: Int, description: Spannable, value: Spannable) {
        val textView = view.findViewById<TextView>(identifier)
        textView.text = description
        textView.append(value)
    }

    fun convertToDeterminationPattern(param1: String, param2: String) =
        String.format(Locale.getDefault(), DETERMINATION_PATTERN, param1, param2)

    fun convertWindDirection(direction: Int): String {
        return when {
            direction <= 11 -> appContext.getString(R.string.wind_direction_N)
            direction <= 34 -> appContext.getString(R.string.wind_direction_NNE)
            direction <= 56 -> appContext.getString(R.string.wind_direction_NE)
            direction <= 79 -> appContext.getString(R.string.wind_direction_ENE)
            direction <= 101 -> appContext.getString(R.string.wind_direction_E)
            direction <= 124 -> appContext.getString(R.string.wind_direction_ESE)
            direction <= 146 -> appContext.getString(R.string.wind_direction_SE)
            direction <= 169 -> appContext.getString(R.string.wind_direction_SSE)
            direction <= 191 -> appContext.getString(R.string.wind_direction_S)
            direction <= 214 -> appContext.getString(R.string.wind_direction_SSW)
            direction <= 236 -> appContext.getString(R.string.wind_direction_SW)
            direction <= 259 -> appContext.getString(R.string.wind_direction_WSW)
            direction <= 281 -> appContext.getString(R.string.wind_direction_W)
            direction <= 304 -> appContext.getString(R.string.wind_direction_WNW)
            direction <= 326 -> appContext.getString(R.string.wind_direction_NW)
            direction <= 349 -> appContext.getString(R.string.wind_direction_NNW)
            else -> appContext.getString(R.string.wind_direction_N)
        }
    }
}