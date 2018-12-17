package by.rudkouski.clockWeatherK.view.weather

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import by.rudkouski.clockWeatherK.R
import by.rudkouski.clockWeatherK.entity.Weather
import by.rudkouski.clockWeatherK.provider.WidgetProvider
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

class WeatherItemView : LinearLayout {

    private var isActualWeather: Boolean = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    companion object {
        private const val DETERMINATION_PATTERN = "%1\$s: %2\$s"
        private const val ENUMERATION_PATTERN = "%1\$s, %2\$s"
        private const val DATE_FORMAT_WITHOUT_YEAR = "dd MMM"
        private const val TIME_FORMAT_24 = "H:mm"
        private const val FULL_TIME_FORMAT_12 = "h:mm a"
        private const val WEATHER_DEGREE_FORMAT = "%1\$d%2\$s"
        private const val KM_IN_MILE = 1.609344
        private const val hPa_IN_Hg = 33.8639
        private const val NOT_UPDATED = " -- "
    }

    fun updateWeatherItemView(weather: Weather?) {
        val view = findViewById<View>(R.id.current_weather)
        isActualWeather = WidgetProvider.isActualWeather(weather)
        setImage(view, weather)
        setDegreeText(view, weather)
        setDescriptionText(view, weather)
        setUpdateDateText(view, weather)
        setFeelText(view, weather)
        setWindText(view, weather)
        setHumidityText(view, weather)
        setPressureText(view, weather)
        setPressureRisingText(view, weather)
        setVisibilityText(view, weather)
        setSunriseText(view, weather)
        setSunsetText(view, weather)
    }

    private fun setImage(view: View, weather: Weather?) {
        val imageView = view.findViewById<ImageView>(R.id.image_current_weather)
        if (isActualWeather) {
            imageView.visibility = VISIBLE
            imageView.setImageResource(WeatherCode.getWeatherImageResourceIdByCode(context, weather!!.code))
        } else {
            imageView.visibility = INVISIBLE
        }
    }

    private fun setDegreeText(view: View, weather: Weather?) {
        val degreeTextView = view.findViewById<TextView>(R.id.degrees_current_weather)
        if (isActualWeather) {
            degreeTextView.visibility = VISIBLE
            val degreeText = String.format(
                Locale.getDefault(),
                WEATHER_DEGREE_FORMAT,
                weather!!.temp,
                context.getString(R.string.temperature_unit)
            )
            degreeTextView.text = degreeText
        } else {
            degreeTextView.visibility = INVISIBLE
        }
    }

    private fun setDescriptionText(view: View, weather: Weather?) {
        val descriptionTextView = view.findViewById<TextView>(R.id.description_current_weather)
        descriptionTextView.text = if (isActualWeather) {
            WeatherCode.getWeatherDescriptionByCode(context, weather!!.code)
        } else {
            context.getString(R.string.default_weather)
        }
    }

    private fun setUpdateDateText(view: View, weather: Weather?) {
        val updateDateTextView = view.findViewById<TextView>(R.id.update_date_current_weather)
        updateDateTextView.visibility = VISIBLE
        val timeFormat = WidgetProvider.chooseSystemTimeFormat(context, FULL_TIME_FORMAT_12, TIME_FORMAT_24)
        val dateWithTimeFormat =
            String.format(Locale.getDefault(), ENUMERATION_PATTERN, DATE_FORMAT_WITHOUT_YEAR, timeFormat)
        val dateFormat = SimpleDateFormat(dateWithTimeFormat, Locale.getDefault())
        val dateText = if (weather?.updateDate != null) dateFormat.format(weather.updateDate.time) else NOT_UPDATED
        updateDateTextView.text = convertToDeterminationPattern(context.getString(R.string.update_date), dateText)
    }

    private fun setFeelText(view: View, weather: Weather?) {
        val description = context.getString(R.string.feel)
        val value = if (isActualWeather) "${convertTemperature(weather!!.windChill)} ${context.getString(
            R.string.temperature_unit)}" else null
        setDataToView(view, R.id.feel_current_weather, description, value)
    }

    private fun setWindText(view: View, weather: Weather?) {
        val description = context.getString(R.string.wind)
        val value = if (isActualWeather) "${convertWindDirection(weather!!.windDirection)}, ${convertWindSpeed(
            weather.windSpeed)} ${context.getString(R.string.speed_unit)}" else null
        setDataToView(view, R.id.wind_current_weather, description, value)
    }

    private fun setHumidityText(view: View, weather: Weather?) {
        val description = context.getString(R.string.humidity)
        val value = if (isActualWeather) "${weather!!.humidity} ${context.getString(R.string.percent_unit)}" else null
        setDataToView(view, R.id.humidity_current_weather, description, value)
    }

    private fun setPressureText(view: View, weather: Weather?) {
        val description = context.getString(R.string.pressure)
        val value = if (isActualWeather) "${convertPressure(weather!!.pressure)} ${context.getString(
            R.string.pressure_unit)}," else null
        setDataToView(view, R.id.pressure_current_weather, description, value)
    }

    private fun setPressureRisingText(view: View, weather: Weather?) {
        val value = if (isActualWeather) convertPressureRising(weather!!.pressureRising) else null
        setDataToView(view, R.id.rising_current_weather, null, value)
    }

    private fun setVisibilityText(view: View, weather: Weather?) {
        val description = context.getString(R.string.visibility)
        val value = if (isActualWeather) "${convertVisibility(weather!!.visibility)} ${context.getString(
            R.string.distance_unit)}" else null
        setDataToView(view, R.id.visibility_current_weather, description, value)
    }

    private fun setSunriseText(view: View, weather: Weather?) {
        val description = context.getString(R.string.sunrise)
        val value = if (isActualWeather) getFormatTime(weather!!.sunrise) else null
        setDataToView(view, R.id.sunrise_current_weather, description, value)
    }

    private fun setSunsetText(view: View, weather: Weather?) {
        val description = context.getString(R.string.sunset)
        val value = if (isActualWeather) getFormatTime(weather!!.sunset) else null
        setDataToView(view, R.id.sunset_current_weather, description, value)
    }

    private fun setDataToView(view: View, identifier: Int, description: String?, value: String?) {
        val textView = view.findViewById<TextView>(identifier)
        if (isActualWeather) {
            textView.visibility = VISIBLE
            textView.text = if (description != null) convertToDeterminationPattern(description, value!!) else value
        } else {
            textView.visibility = INVISIBLE
        }
    }

    private fun convertToDeterminationPattern(param1: String, param2: String): String {
        return String.format(Locale.getDefault(), DETERMINATION_PATTERN, param1, param2)
    }

    private fun convertTemperature(temperature: Int): Long {
        return Math.round((temperature - 32) * 5.0 / 9.0)
    }

    private fun convertWindDirection(direction: Int): String {
        return when {
            direction <= 11 -> context.getString(R.string.wind_direction_N)
            direction <= 34 -> context.getString(R.string.wind_direction_NNE)
            direction <= 56 -> context.getString(R.string.wind_direction_NE)
            direction <= 79 -> context.getString(R.string.wind_direction_ENE)
            direction <= 101 -> context.getString(R.string.wind_direction_E)
            direction <= 124 -> context.getString(R.string.wind_direction_ESE)
            direction <= 146 -> context.getString(R.string.wind_direction_SE)
            direction <= 169 -> context.getString(R.string.wind_direction_SSE)
            direction <= 191 -> context.getString(R.string.wind_direction_S)
            direction <= 214 -> context.getString(R.string.wind_direction_SSW)
            direction <= 236 -> context.getString(R.string.wind_direction_SW)
            direction <= 259 -> context.getString(R.string.wind_direction_WSW)
            direction <= 281 -> context.getString(R.string.wind_direction_W)
            direction <= 304 -> context.getString(R.string.wind_direction_WNW)
            direction <= 326 -> context.getString(R.string.wind_direction_NW)
            direction <= 349 -> context.getString(R.string.wind_direction_NNW)
            else -> context.getString(R.string.wind_direction_N)
        }
    }

    private fun convertWindSpeed(speed: Double): Long {
        return Math.round(speed / KM_IN_MILE)
    }

    private fun convertPressure(pressure: Double): Long {
        return Math.round(pressure / hPa_IN_Hg)
    }

    private fun convertPressureRising(rising: Int): String {
        return context.getString(
            when (rising) {
                0 -> R.string.pressure_steady
                1 -> R.string.pressure_rising
                else -> R.string.pressure_falling
            }
        )
    }

    private fun convertVisibility(visibility: Double): Double {
        return (visibility / KM_IN_MILE).roundTo2DecimalPlaces()
    }

    private fun getFormatTime(date: Date): String {
        val timeFormat = WidgetProvider.chooseSystemTimeFormat(context, FULL_TIME_FORMAT_12, TIME_FORMAT_24)
        val dateFormat = SimpleDateFormat(timeFormat, Locale.getDefault())
        return dateFormat.format(date)
    }

    private fun Double.roundTo2DecimalPlaces() = BigDecimal(this).setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
}