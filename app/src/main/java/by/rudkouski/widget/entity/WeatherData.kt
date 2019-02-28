package by.rudkouski.widget.entity

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Contains the common fields for weather conditions.
 */
abstract class WeatherData(val id: Long,
                           @SerializedName("time") val date: Calendar,
                           @SerializedName("summary") val description: String,
                           @SerializedName("icon") val iconName: String,
                           @SerializedName("precipIntensity") val precipitationIntensity: Double,
                           @SerializedName("precipProbability") val precipitationProbability: Double,
                           @SerializedName("dewPoint") val dewPoint: Double,
                           @SerializedName("humidity") val humidity: Double,
                           @SerializedName("pressure") val pressure: Double,
                           @SerializedName("windSpeed") val windSpeed: Double,
                           @SerializedName("windGust") val windGust: Double,
                           @SerializedName("windBearing") val windDirection: Int,
                           @SerializedName("cloudCover") val cloudCover: Double,
                           @SerializedName("visibility") val visibility: Double,
                           @SerializedName("ozone") val ozone: Double,
                           @SerializedName("uvIndex") val uvIndex: Int)