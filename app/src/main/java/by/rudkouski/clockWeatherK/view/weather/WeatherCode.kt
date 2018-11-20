package by.rudkouski.clockWeatherK.view.weather

import android.content.Context
import android.util.Log
import android.util.SparseArray
import by.rudkouski.clockWeatherK.R

abstract class WeatherCode {

    companion object {
        private val WEATHER_CODES = SparseArray<String>()

        init {
            with(WEATHER_CODES) {
                put(0, "tornado")
                put(1, "tropical_storm")
                put(2, "hurricane")
                put(3, "severe_thunderstorms")
                put(4, "thunderstorms")
                put(5, "mixed_rain_and_snow")
                put(6, "mixed_rain_and_sleet")
                put(7, "mixed_snow_and_sleet")
                put(8, "freezing_drizzle")
                put(9, "drizzle")
                put(10, "freezing_rain")
                put(11, "showers_1")
                put(12, "showers_2")
                put(13, "snow_flurries")
                put(14, "light_snow_showers")
                put(15, "blowing_snow11")
                put(16, "snow")
                put(17, "hail")
                put(18, "sleet")
                put(19, "dust")
                put(20, "foggy")
                put(21, "haze")
                put(22, "smoky")
                put(23, "blustery")
                put(24, "windy")
                put(25, "cold")
                put(26, "cloudy")
                put(27, "mostly_cloudy_night")
                put(28, "mostly_cloudy_day")
                put(29, "partly_cloudy_night")
                put(30, "partly_cloudy_day")
                put(31, "clear_night")
                put(32, "sunny")
                put(33, "fair_night")
                put(34, "fair_day")
                put(35, "mixed_rain_and_hail")
                put(36, "hot")
                put(37, "isolated_thunderstorms")
                put(38, "scattered_thunderstorms_1")
                put(39, "scattered_thunderstorms_2")
                put(40, "scattered_showers")
                put(41, "heavy_snow_1")
                put(42, "scattered_snow_showers")
                put(43, "heavy_snow_2")
                put(44, "partly_cloudy")
                put(45, "thundershowers")
                put(46, "snow_showers")
                put(47, "isolated_thundershowers")
            }
        }

        fun getWeatherDescriptionByCode(context: Context, code: Int): String {
            return try {
                val resourceName = WEATHER_CODES.get(code)
                context.getString(
                    context.resources.getIdentifier(
                        resourceName,
                        String::class.java.simpleName.toLowerCase(),
                        context.packageName
                    )
                )
            } catch (e: ArrayIndexOutOfBoundsException) {
                Log.e(WeatherCode::class.java.simpleName, e.toString())
                context.getString(R.string.default_weather)
            }
        }

        fun getWeatherImageResourceIdByCode(context: Context, code: Int): Int {
            return try {
                val resourceName = WEATHER_CODES.get(code)
                context.resources.getIdentifier(resourceName, "mipmap", context.packageName)

            } catch (e: ArrayIndexOutOfBoundsException) {
                Log.e(WeatherCode::class.java.simpleName, e.toString())
                0
            }
        }
    }
}