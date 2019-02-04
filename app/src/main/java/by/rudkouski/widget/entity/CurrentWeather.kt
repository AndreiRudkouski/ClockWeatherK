package by.rudkouski.widget.entity

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Contains the current weather conditions at the requested location.
 */
class CurrentWeather : WeatherData {

    @SerializedName("temperature")
    val temperature: Double

    @SerializedName("apparentTemperature")
    val apparentTemperature: Double

    constructor(id: Long, date: Date, description: String, iconName: String, precipitationIntensity: Double,
                precipitationProbability: Double, dewPoint: Double, humidity: Double, pressure: Double,
                windSpeed: Double, windGust: Double, windDirection: Int, cloudCover: Double, visibility: Double,
                ozone: Double, uvIndex: Int, temperature: Double, apparentTemperature: Double) :
        super(id, date, description, iconName, precipitationIntensity, precipitationProbability, dewPoint, humidity,
            pressure, windSpeed, windGust, windDirection, cloudCover, visibility, ozone, uvIndex) {
        this.temperature = temperature
        this.apparentTemperature = apparentTemperature
    }

    constructor(newId: Long, weather: CurrentWeather) :
        this(newId, weather.date, weather.description, weather.iconName, weather.precipitationIntensity,
            weather.precipitationProbability, weather.dewPoint, weather.humidity, weather.pressure, weather.windSpeed,
            weather.windGust, weather.windDirection, weather.cloudCover, weather.visibility, weather.ozone,
            weather.uvIndex, weather.temperature, weather.apparentTemperature)
}