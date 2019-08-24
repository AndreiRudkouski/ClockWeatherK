package by.rudkouski.widget.entity

import androidx.room.*
import by.rudkouski.widget.database.converter.CalendarConverter
import by.rudkouski.widget.database.converter.WeatherTypeConverter
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Contains the weather conditions at the requested location.
 */
@Entity(tableName = "weathers",
    foreignKeys = [(ForeignKey(entity = Location::class, parentColumns = ["location_id"], childColumns = ["weather_location_id"]))],
    indices = [Index(value = ["weather_location_id"])])
@TypeConverters(CalendarConverter::class, WeatherTypeConverter::class)
data class Weather(@PrimaryKey(autoGenerate = true)
                   @ColumnInfo(name = "weather_id")
                   val id: Int,
                   @SerializedName("time")
                   @ColumnInfo(name = "weather_date")
                   val date: Calendar,
                   @SerializedName("summary")
                   @ColumnInfo(name = "weather_description")
                   val description: String,
                   @SerializedName("icon")
                   @ColumnInfo(name = "weather_icon")
                   val iconName: String,
                   @SerializedName("precipIntensity")
                   @ColumnInfo(name = "weather_precip_intensity")
                   val precipitationIntensity: Double,
                   @SerializedName("precipProbability")
                   @ColumnInfo(name = "weather_precip_probability")
                   val precipitationProbability: Double,
                   @SerializedName("dewPoint")
                   @ColumnInfo(name = "weather_dew_point")
                   val dewPoint: Double,
                   @SerializedName("humidity")
                   @ColumnInfo(name = "weather_humidity")
                   val humidity: Double,
                   @SerializedName("pressure")
                   @ColumnInfo(name = "weather_pressure")
                   val pressure: Double,
                   @SerializedName("windSpeed")
                   @ColumnInfo(name = "weather_wind_speed")
                   val windSpeed: Double,
                   @SerializedName("windGust")
                   @ColumnInfo(name = "weather_wind_gust")
                   val windGust: Double,
                   @SerializedName("windBearing")
                   @ColumnInfo(name = "weather_wind_direction")
                   val windDirection: Int,
                   @SerializedName("cloudCover")
                   @ColumnInfo(name = "weather_cloud_cover")
                   val cloudCover: Double,
                   @SerializedName("visibility")
                   @ColumnInfo(name = "weather_visibility")
                   val visibility: Double,
                   @SerializedName("ozone")
                   @ColumnInfo(name = "weather_ozone")
                   val ozone: Double,
                   @SerializedName("uvIndex")
                   @ColumnInfo(name = "weather_uv_index")
                   val uvIndex: Int,
                   @SerializedName("temperature")
                   @ColumnInfo(name = "weather_temp")
                   val temperature: Double,
                   @SerializedName("apparentTemperature")
                   @ColumnInfo(name = "weather_apparent_temp")
                   val apparentTemperature: Double,
                   @ColumnInfo(name = "weather_location_id")
                   var locationId: Int? = null,
                   @ColumnInfo(name = "weather_type")
                   var type: WeatherType = WeatherType.HOUR) {

    enum class WeatherType {
        HOUR,
        CURRENT
    }
}

