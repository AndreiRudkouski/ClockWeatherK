package by.rudkouski.clockWeatherK.entity

import com.google.gson.annotations.SerializedName
import java.util.*

class Weather {

    val id: Int

    @SerializedName("time")
    val date: Date


    @SerializedName("summary")
    val description: String

    @SerializedName("icon")
    val iconName: String

    @SerializedName("precipIntensity")
    val precipitationIntensity: Double

    @SerializedName("precipProbability")
    val precipitationProbability: Double

    @SerializedName("temperature")
    val temperature: Double

    @SerializedName("apparentTemperature")
    val apparentTemperature: Double

    @SerializedName("dewPoint")
    val dewPoint: Double

    @SerializedName("humidity")
    val humidity: Double

    @SerializedName("pressure")
    val pressure: Double

    @SerializedName("windSpeed")
    val windSpeed: Double

    @SerializedName("windGust")
    val windGust: Double

    @SerializedName("windBearing")
    val windDirection: Int

    @SerializedName("cloudCover")
    val cloudCover: Double

    @SerializedName("uvIndex")
    val uvIndex: Int

    @SerializedName("visibility")
    val visibility: Double

    @SerializedName("ozone")
    val ozone: Double

    constructor(id: Int, date: Date, description: String, iconName: String, precipitationIntensity: Double,
                precipitationProbability: Double, temperature: Double, apparentTemperature: Double, dewPoint: Double,
                humidity: Double, pressure: Double, windSpeed: Double, windGust: Double, windDirection: Int,
                cloudCover: Double, uvIndex: Int, visibility: Double, ozone: Double) {
        this.id = id
        this.date = date
        this.description = description
        this.iconName = iconName
        this.precipitationIntensity = precipitationIntensity
        this.precipitationProbability = precipitationProbability
        this.temperature = temperature
        this.apparentTemperature = apparentTemperature
        this.dewPoint = dewPoint
        this.humidity = humidity
        this.pressure = pressure
        this.windSpeed = windSpeed
        this.windGust = windGust
        this.windDirection = windDirection
        this.cloudCover = cloudCover
        this.uvIndex = uvIndex
        this.visibility = visibility
        this.ozone = ozone
    }

    constructor(newId: Int, weather: Weather) :
        this(newId, weather.date, weather.description, weather.iconName, weather.precipitationIntensity,
            weather.precipitationProbability, weather.temperature, weather.apparentTemperature, weather.dewPoint,
            weather.humidity, weather.pressure, weather.windSpeed, weather.windGust, weather.windDirection,
            weather.cloudCover, weather.uvIndex, weather.visibility, weather.ozone)
}