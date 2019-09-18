package by.rudkouski.widget.util

import by.rudkouski.widget.entity.Forecast
import by.rudkouski.widget.entity.Weather
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.threeten.bp.Instant.ofEpochMilli
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneId.of

object JsonUtils {

    private const val CURRENT_WEATHER = "currently"
    private const val HOUR_WEATHER = "hourly"
    private const val DAY_WEATHER = "daily"
    private const val LIST_DATA = "data"

    fun getCurrentZoneIdFromResponseBody(responseBody: String): ZoneId {
        val json = GsonBuilder().create()
        val jsonObject = json.fromJson(responseBody, JsonObject::class.java)
        return of(jsonObject.get("timezone").asString)
    }

    fun getCurrentWeatherFromResponseBody(responseBody: String, locationId: Int, zoneId: ZoneId, update: OffsetDateTime): Weather {
        val gson = getGson(zoneId)
        val jsonObject = gson.fromJson(responseBody, JsonObject::class.java).getAsJsonObject(CURRENT_WEATHER)
        return gson.fromJson(jsonObject, Weather::class.java).also { it.locationId = locationId }.also { it.type = Weather.Type.CURRENT }
            .also { it.update = update }
    }

    fun getHourWeathersFromResponseBody(responseBody: String, locationId: Int, zoneId: ZoneId, update: OffsetDateTime): List<Weather> {
        val gson = getGson(zoneId)
        val jsonArray = gson.fromJson(responseBody, JsonObject::class.java).getAsJsonObject(HOUR_WEATHER).getAsJsonArray(
            LIST_DATA)
        val weathers: List<Weather> = gson.fromJson(jsonArray, object : TypeToken<List<Weather>>() {}.type)
        weathers.forEach {
            it.locationId = locationId
            it.type = Weather.Type.HOUR
            it.update = update
        }
        return weathers
    }

    fun getDayForecastFromResponseBody(responseBody: String, locationId: Int, zoneId: ZoneId): List<Forecast> {
        val gson = getGson(zoneId)
        val jsonArray = gson.fromJson(responseBody, JsonObject::class.java).getAsJsonObject(DAY_WEATHER).getAsJsonArray(
            LIST_DATA)
        val forecasts: List<Forecast> = gson.fromJson(jsonArray, object : TypeToken<List<Forecast>>() {}.type)
        forecasts.forEach { it.locationId = locationId }
        return forecasts
    }

    private fun getGson(zoneId: ZoneId) = GsonBuilder().registerTypeAdapter(OffsetDateTime::class.java,
        getDateJsonDeserializer(zoneId)).create()

    private fun getDateJsonDeserializer(zoneId: ZoneId): JsonDeserializer<OffsetDateTime> {
        return JsonDeserializer { json, _, _ ->
            ofEpochMilli(json.asJsonPrimitive.asLong * 1000).atZone(zoneId).toOffsetDateTime()
        }
    }
}