package by.rudkouski.clockWeatherK.entity

import com.google.gson.annotations.SerializedName
import java.util.*

class Forecast {

    val id: Int

    /*The condition code for this forecast*/
    @SerializedName("code")
    val code: Int

    /*The date to which this forecast applies. The date is in "dd Mmm yyyy" format*/
    @SerializedName("date")
    val date: Date

    @SerializedName("high")
    val highTemp: Int

    @SerializedName("low")
    val lowTemp: Int

    constructor(id: Int, code: Int, date: Date, highTemp: Int, lowTemp: Int) {
        this.id = id
        this.code = code
        this.date = date
        this.highTemp = highTemp
        this.lowTemp = lowTemp
    }

    constructor(newId: Int, forecast: Forecast) : this(newId, forecast.code, forecast.date, forecast.highTemp,
        forecast.lowTemp)
}