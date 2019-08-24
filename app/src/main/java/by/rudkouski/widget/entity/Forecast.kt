package by.rudkouski.widget.entity

import androidx.room.*
import by.rudkouski.widget.database.converter.CalendarConverter
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Contains the date weather conditions at the requested location.
 */
@Entity(tableName = "forecasts",
    foreignKeys = [(ForeignKey(entity = Location::class, parentColumns = ["location_id"], childColumns = ["forecast_location_id"]))],
    indices = [Index(value = ["forecast_location_id"])])
@TypeConverters(CalendarConverter::class)
data class Forecast(@PrimaryKey(autoGenerate = true)
                    @ColumnInfo(name = "forecast_id")
                    val id: Int,
                    @SerializedName("time")
                    @ColumnInfo(name = "forecast_date")
                    val date: Calendar,
                    @SerializedName("summary")
                    @ColumnInfo(name = "forecast_description")
                    val description: String,
                    @SerializedName("icon")
                    @ColumnInfo(name = "forecast_icon")
                    val iconName: String,
                    @SerializedName("precipIntensity")
                    @ColumnInfo(name = "forecast_precip_intensity")
                    val precipitationIntensity: Double,
                    @SerializedName("precipProbability")
                    @ColumnInfo(name = "forecast_precip_probability")
                    val precipitationProbability: Double,
                    @SerializedName("dewPoint")
                    @ColumnInfo(name = "forecast_dew_point")
                    val dewPoint: Double,
                    @SerializedName("humidity")
                    @ColumnInfo(name = "forecast_humidity")
                    val humidity: Double,
                    @SerializedName("pressure")
                    @ColumnInfo(name = "forecast_pressure")
                    val pressure: Double,
                    @SerializedName("windSpeed")
                    @ColumnInfo(name = "forecast_wind_speed")
                    val windSpeed: Double,
                    @SerializedName("windGust")
                    @ColumnInfo(name = "forecast_wind_gust")
                    val windGust: Double,
                    @SerializedName("windBearing")
                    @ColumnInfo(name = "forecast_wind_direction")
                    val windDirection: Int,
                    @SerializedName("cloudCover")
                    @ColumnInfo(name = "forecast_cloud_cover")
                    val cloudCover: Double,
                    @SerializedName("visibility")
                    @ColumnInfo(name = "forecast_visibility")
                    val visibility: Double,
                    @SerializedName("ozone")
                    @ColumnInfo(name = "forecast_ozone")
                    val ozone: Double,
                    @SerializedName("uvIndex")
                    @ColumnInfo(name = "forecast_uv_index")
                    val uvIndex: Int,
                    @SerializedName("sunriseTime")
                    @ColumnInfo(name = "forecast_sunrise_time")
                    val sunriseTime: Calendar,
                    @SerializedName("sunsetTime")
                    @ColumnInfo(name = "forecast_sunset_time")
                    val sunsetTime: Calendar,
                    @SerializedName("moonPhase")
                    @ColumnInfo(name = "forecast_moon_phase")
                    val moonPhase: Double,
                    @SerializedName("precipIntensityMax")
                    @ColumnInfo(name = "forecast_precip_intensity_max")
                    val precipitationIntensityMax: Double?,
                    @SerializedName("precipIntensityMaxTime")
                    @ColumnInfo(name = "forecast_precip_intensity_max_time")
                    val precipitationIntensityMaxTime: Calendar?,
                    @SerializedName("precipAccumulation")
                    @ColumnInfo(name = "forecast_precip_accumulation")
                    val precipitationAccumulation: Double?,
                    @SerializedName("precipType")
                    @ColumnInfo(name = "forecast_precip_type")
                    val precipitationType: String?,
                    @SerializedName("temperatureHigh")
                    @ColumnInfo(name = "forecast_temp_high")
                    val temperatureHigh: Double,
                    @SerializedName("temperatureHighTime")
                    @ColumnInfo(name = "forecast_temp_high_time")
                    val temperatureHighTime: Calendar,
                    @SerializedName("temperatureLow")
                    @ColumnInfo(name = "forecast_temp_low")
                    val temperatureLow: Double,
                    @SerializedName("temperatureLowTime")
                    @ColumnInfo(name = "forecast_temp_low_time")
                    val temperatureLowTime: Calendar,
                    @SerializedName("apparentTemperatureHigh")
                    @ColumnInfo(name = "forecast_apparent_temp_high")
                    val apparentTemperatureHigh: Double,
                    @SerializedName("apparentTemperatureHighTime")
                    @ColumnInfo(name = "forecast_apparent_temp_high_time")
                    val apparentTemperatureHighTime: Calendar,
                    @SerializedName("apparentTemperatureLow")
                    @ColumnInfo(name = "forecast_apparent_temp_low")
                    val apparentTemperatureLow: Double,
                    @SerializedName("apparentTemperatureLowTime")
                    @ColumnInfo(name = "forecast_apparent_temp_low_time")
                    val apparentTemperatureLowTime: Calendar,
                    @ColumnInfo(name = "forecast_location_id")
                    var locationId: Int? = null)