package by.rudkouski.widget.view.weather

import android.content.Context
import android.text.Spannable
import android.view.View
import android.widget.TextView
import by.rudkouski.widget.R
import by.rudkouski.widget.app.App.Companion.appContext
import by.rudkouski.widget.entity.Forecast
import by.rudkouski.widget.entity.Weather
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import java.util.*
import java.util.TimeZone.getTimeZone
import kotlin.math.roundToInt

object WeatherUtils {

    private const val CURRENT_WEATHER = "currently"
    private const val HOUR_WEATHER = "hourly"
    private const val DAY_WEATHER = "daily"
    private const val LIST_DATA = "data"

    private const val WEATHER_DEGREE_FORMAT = "%1\$d%2\$s"
    private const val DETERMINATION_PATTERN = "%1\$s: %2\$s"

    private val dateJsonDeserializer: JsonDeserializer<Calendar> =
        JsonDeserializer { json, _, _ ->
            val date = Calendar.getInstance()
            date.time = Date(json.asJsonPrimitive.asLong * 1000)
            return@JsonDeserializer date
        }

    private val gson = GsonBuilder().registerTypeAdapter(Calendar::class.java, dateJsonDeserializer).create()

    fun getCurrentTimeZoneNameFromResponseBody(responseBody: String): TimeZone {
        val jsonObject = gson.fromJson(responseBody, JsonObject::class.java)
        return getTimeZone(jsonObject.get("timezone").asString)
    }

    fun getCurrentWeatherFromResponseBody(responseBody: String): Weather {
        val jsonObject = gson.fromJson(responseBody, JsonObject::class.java).getAsJsonObject(CURRENT_WEATHER)
        return gson.fromJson(jsonObject, Weather::class.java)
    }

    fun getHourWeathersFromResponseBody(responseBody: String): List<Weather> {
        val jsonArray = gson.fromJson(responseBody, JsonObject::class.java).getAsJsonObject(HOUR_WEATHER).getAsJsonArray(LIST_DATA)
        val type = object : TypeToken<List<Weather>>(){}.type
        return gson.fromJson(jsonArray, type)
    }

    fun getDayForecastFromResponseBody(responseBody: String): List<Forecast> {
        val jsonArray = gson.fromJson(responseBody, JsonObject::class.java).getAsJsonObject(DAY_WEATHER).getAsJsonArray(LIST_DATA)
        val type = object : TypeToken<List<Forecast>>(){}.type
        return gson.fromJson(jsonArray, type)
    }

    fun getIconWeatherImageResource(context: Context, iconName: String, cloudCover: Double, precipitationProbability: Double): Int {
        var postFix = ""
        var preFix = ""
        if (iconName.startsWith("partly-cloudy")) {
            if (cloudCover >= 0.6) postFix = "_mostly"
            if (cloudCover < 0.4) postFix = "_less"
        }
        if (iconName.startsWith("rain")) {
            if (cloudCover < 0.6) {
                preFix = if (cloudCover < 0.4) "less_" else "mostly_"
            } else {
                postFix = if (precipitationProbability >= 0.5) "_mostly" else "_less"
            }
        }
        return context.resources.getIdentifier(preFix + iconName.replace("-", "_") + postFix, "mipmap",
            context.packageName)
    }

    fun convertDoubleToPercents(double: Double) =
        "${mathRound(double * 100)}${appContext.getString(R.string.percent_unit)}"

    fun getDegreeText(temperature: Double) =
        String.format(Locale.getDefault(), WEATHER_DEGREE_FORMAT, mathRound(temperature),
            appContext.getString(R.string.temperature_unit))

    fun mathRound(double: Double) = double.roundToInt()

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