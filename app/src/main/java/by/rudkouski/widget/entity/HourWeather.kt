package by.rudkouski.widget.entity

import com.google.gson.annotations.SerializedName

class HourWeather(val id: Long,
                  @SerializedName("data") val weathers: List<Weather>)