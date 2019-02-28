package by.rudkouski.widget.entity

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Contains the date weather conditions at the requested location.
 */
class Forecast : WeatherData {

    @SerializedName("sunriseTime")
    val sunriseTime: Calendar

    @SerializedName("sunsetTime")
    val sunsetTime: Calendar

    @SerializedName("moonPhase")
    val moonPhase: Double

    @SerializedName("precipIntensityMax")
    val precipitationIntensityMax: Double?

    @SerializedName("precipIntensityMaxTime")
    val precipitationIntensityMaxTime: Calendar?

    @SerializedName("precipAccumulation")
    val precipitationAccumulation: Double?

    @SerializedName("precipType")
    val precipitationType: String?

    @SerializedName("temperatureHigh")
    val temperatureHigh: Double

    @SerializedName("temperatureHighTime")
    val temperatureHighTime: Calendar

    @SerializedName("temperatureLow")
    val temperatureLow: Double

    @SerializedName("temperatureLowTime")
    val temperatureLowTime: Calendar

    @SerializedName("apparentTemperatureHigh")
    val apparentTemperatureHigh: Double

    @SerializedName("apparentTemperatureHighTime")
    val apparentTemperatureHighTime: Calendar

    @SerializedName("apparentTemperatureLow")
    val apparentTemperatureLow: Double

    @SerializedName("apparentTemperatureLowTime")
    val apparentTemperatureLowTime: Calendar

    constructor(id: Long, date: Calendar, description: String, iconName: String, precipitationIntensity: Double,
                precipitationProbability: Double, dewPoint: Double, humidity: Double, pressure: Double,
                windSpeed: Double, windGust: Double, windDirection: Int, cloudCover: Double, visibility: Double,
                ozone: Double, uvIndex: Int, sunriseTime: Calendar, sunsetTime: Calendar, moonPhase: Double,
                precipitationIntensityMax: Double?, precipitationIntensityMaxTime: Calendar?,
                precipitationAccumulation: Double?, precipitationType: String?, temperatureHigh: Double,
                temperatureHighTime: Calendar, temperatureLow: Double, temperatureLowTime: Calendar,
                apparentTemperatureHigh: Double, apparentTemperatureHighTime: Calendar, apparentTemperatureLow: Double,
                apparentTemperatureLowTime: Calendar) : super(id, date, description, iconName, precipitationIntensity,
        precipitationProbability, dewPoint, humidity, pressure, windSpeed, windGust, windDirection, cloudCover,
        visibility, ozone, uvIndex) {
        this.sunriseTime = sunriseTime
        this.sunsetTime = sunsetTime
        this.moonPhase = moonPhase
        this.precipitationIntensityMax = precipitationIntensityMax
        this.precipitationIntensityMaxTime = precipitationIntensityMaxTime
        this.precipitationAccumulation = precipitationAccumulation
        this.precipitationType = precipitationType
        this.temperatureHigh = temperatureHigh
        this.temperatureHighTime = temperatureHighTime
        this.temperatureLow = temperatureLow
        this.temperatureLowTime = temperatureLowTime
        this.apparentTemperatureHigh = apparentTemperatureHigh
        this.apparentTemperatureHighTime = apparentTemperatureHighTime
        this.apparentTemperatureLow = apparentTemperatureLow
        this.apparentTemperatureLowTime = apparentTemperatureLowTime
    }

    constructor(newId: Long, forecast: Forecast) :
        this(newId, forecast.date, forecast.description, forecast.iconName, forecast.precipitationIntensity,
            forecast.precipitationProbability, forecast.dewPoint, forecast.humidity, forecast.pressure,
            forecast.windSpeed, forecast.windGust, forecast.windDirection, forecast.cloudCover, forecast.visibility,
            forecast.ozone, forecast.uvIndex, forecast.sunriseTime, forecast.sunsetTime, forecast.moonPhase,
            forecast.precipitationIntensityMax, forecast.precipitationIntensityMaxTime,
            forecast.precipitationAccumulation, forecast.precipitationType, forecast.temperatureHigh,
            forecast.temperatureHighTime, forecast.temperatureLow, forecast.temperatureLowTime,
            forecast.apparentTemperatureHigh, forecast.apparentTemperatureHighTime, forecast.apparentTemperatureLow,
            forecast.apparentTemperatureLowTime)
}