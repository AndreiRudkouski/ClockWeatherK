package by.rudkouski.widget.entity

import com.google.gson.annotations.SerializedName

/**
 * Contains the weather conditions day-by-day for the next week.
 */
class DayForecast(val id: Long,
                  @SerializedName("summary") val description: String,
                  @SerializedName("icon") val iconName: String,
                  @SerializedName("data") val forecasts: List<Forecast>)