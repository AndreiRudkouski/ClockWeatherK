package by.rudkouski.clockWeatherK.entity

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class Weather {

    val id: Int

    /*The condition code for this forecast*/
    @SerializedName("code")
    val code: Int

    /*The current date and time for which this forecast applies. The format is "E, dd Mmm yyyy
    hh:mm aa"*/
    @SerializedName("date")
    val createDate: Date

    @SerializedName("temp")
    val temp: Int

    /*Wind chill in degrees (integer)*/
    @SerializedName("chill")
    val windChill: Int

    /*Wind direction, in degrees (integer)*/
    @SerializedName("direction")
    val windDirection: Int

    @SerializedName("speed")
    val windSpeed: Double

    /*Humidity, in percent (integer)*/
    @SerializedName("humidity")
    val humidity: Int

    @SerializedName("pressure")
    val pressure: Double

    /*State of the barometric pressure: steady (0), rising (1), or falling (2). (integer: 0, 1, 2)*/
    @SerializedName("rising")
    val pressureRising: Int

    @SerializedName("visibility")
    val visibility: Double

    @SerializedName("sunrise")
    val sunrise: Date

    @SerializedName("sunset")
    val sunset: Date

    @Expose val updateDate: Date?

    constructor(id: Int, code: Int, createDate: Date, temp: Int, windChill: Int, windDirection: Int, windSpeed: Double,
                humidity: Int,
                pressure: Double,
                pressureRising: Int, visibility: Double, sunrise: Date, sunset: Date, updateDate: Date) {
        this.id = id
        this.code = code
        this.createDate = createDate
        this.temp = temp
        this.windChill = windChill
        this.windDirection = windDirection
        this.windSpeed = windSpeed
        this.humidity = humidity
        this.pressure = pressure
        this.pressureRising = pressureRising
        this.visibility = visibility
        this.sunrise = sunrise
        this.sunset = sunset
        this.updateDate = updateDate
    }

    constructor(newId: Int, weather: Weather, updateDate: Date) :
        this(newId, weather.code, weather.createDate, weather.temp, weather.windChill, weather.windDirection,
            weather.windSpeed, weather.humidity, weather.pressure, weather.pressureRising, weather.visibility,
            weather.sunrise, weather.sunset, updateDate)
}