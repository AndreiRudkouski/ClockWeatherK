package by.rudkouski.clockWeatherK.view.weather

import by.rudkouski.clockWeatherK.entity.Forecast
import by.rudkouski.clockWeatherK.entity.Weather
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import java.text.SimpleDateFormat
import java.util.*

object WeatherJsonConverter {

    private const val FULL_TIME_FORMAT_12 = "h:mm a"
    private const val FULL_DATE_AND_TIME_FORMAT = "EEE, dd MMM yyyy h:mm a"
    private const val DATE_FORMAT = "dd MMM yyyy"
    private const val FORECAST_DELIMITER = ",\"forecast\":"
    private const val DESCRIPTION_DELIMITER = ",\"description\""
    private const val WIND_DELIMITER = "\"wind\":"
    private const val ATMOSPHERE_DELIMITER = "\\}\\,\"atmosphere\":\\{"
    private const val ASTRONOMY_DELIMITER = "\\}\\,\"astronomy\":\\{"
    private const val IMAGE_DELIMITER = "\\}\\,\"image\":\\{"
    private const val CONDITION_DELIMITER = "\"condition\":\\{"

    fun getWeatherFromResponseBody(responseBody: String): Weather {
        val dateJsonDeserializer: JsonDeserializer<Date> =
            JsonDeserializer { json, _, _ ->
                val date = json.asString
                val format: SimpleDateFormat
                format = if (date.length < FULL_DATE_AND_TIME_FORMAT.length) {
                    SimpleDateFormat(FULL_TIME_FORMAT_12, Locale.US)
                } else {
                    SimpleDateFormat(FULL_DATE_AND_TIME_FORMAT, Locale.US)
                }
                format.parse(date)
            }

        val currentWeatherJson = parseResponseBodyToWeatherJson(responseBody)
        val gson = GsonBuilder().registerTypeAdapter(Date::class.java, dateJsonDeserializer).create()
        return gson.fromJson(currentWeatherJson, Weather::class.java)
    }

    private fun parseResponseBodyToWeatherJson(responseBody: String): String {
        val divisionByWind =
            responseBody.split(FORECAST_DELIMITER.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].split(
                WIND_DELIMITER.toRegex()
            ).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        val twiceDivision = divisionByWind.replaceFirst(ATMOSPHERE_DELIMITER.toRegex(), ",")
            .replaceFirst(ASTRONOMY_DELIMITER.toRegex(), ",")
        val divisionByImage =
            twiceDivision.split(IMAGE_DELIMITER.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return divisionByImage[0] + "," + divisionByImage[1].split(
            CONDITION_DELIMITER.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
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
}