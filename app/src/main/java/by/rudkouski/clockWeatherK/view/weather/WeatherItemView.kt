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
        private const val KM_IN_MILE = 1.60934
        private const val ACCURACY = 100 / 100.0
        private const val hPa_IN_Hg = 33.8639
    }

    fun updateWeatherItemView(weather: Weather?) {
        val view = findViewById<View>(R.id.current_weather)
        isActualWeather = WidgetProvider.isActualWeather(weather)
        setImage(view, weather)
        setDegreeText(view, weather)
        setDescriptionText(view, weather)
        setCreateDateText(view, weather)
        setWindDirectionText(view, weather)
        setWindSpeedText(view, weather)
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
                context.getString(R.string.degree)
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

    private fun setCreateDateText(view: View, weather: Weather?) {
        val createDateTextView = view.findViewById<TextView>(R.id.create_current_weather)
        if (isActualWeather) {
            createDateTextView.visibility = VISIBLE
            val timeFormat = WidgetProvider.chooseSystemTimeFormat(context, FULL_TIME_FORMAT_12, TIME_FORMAT_24)
            val dateWithTimeFormat =
                String.format(Locale.getDefault(), ENUMERATION_PATTERN, DATE_FORMAT_WITHOUT_YEAR, timeFormat)
            val dateFormat = SimpleDateFormat(dateWithTimeFormat, Locale.getDefault())
            val dateText = dateFormat.format(weather!!.createDate.time)
            createDateTextView.text = convertToDeterminationPattern(context.getString(R.string.apply), dateText)
        } else {
            createDateTextView.visibility = INVISIBLE
        }
    }

    private fun setWindDirectionText(view: View, weather: Weather?) {
        val description = context.getString(R.string.wind_direction)
        val value = weather!!.windDirection.toString()
        setDataToView(view, R.id.direction_current_weather, description, value)
    }

    private fun setWindSpeedText(view: View, weather: Weather?) {
        val description = context.getString(R.string.wind_speed)
        val value = weather!!.windSpeed.toString()
        setDataToView(view, R.id.speed_current_weather, description, value)
    }

    private fun setHumidityText(view: View, weather: Weather?) {
        val description = context.getString(R.string.humidity)
        val value = weather!!.humidity.toString()
        setDataToView(view, R.id.humidity_current_weather, description, value)
    }

    private fun setPressureText(view: View, weather: Weather?) {
        val description = context.getString(R.string.pressure)
        val value = weather!!.pressure.toString()
        setDataToView(view, R.id.pressure_current_weather, description, value)
    }

    private fun setPressureRisingText(view: View, weather: Weather?) {
        val description = context.getString(R.string.rising)
        val value = weather!!.pressureRising.toString()
        setDataToView(view, R.id.rising_current_weather, description, value)
    }

    private fun setVisibilityText(view: View, weather: Weather?) {
        val description = context.getString(R.string.visibility)
        val value = weather!!.visibility.toString()
        setDataToView(view, R.id.visibility_current_weather, description, value)
    }

    private fun setSunriseText(view: View, weather: Weather?) {
        val description = context.getString(R.string.sunrise)
        val value = getFormatTime(weather!!.sunrise)
        setDataToView(view, R.id.sunrise_current_weather, description, value)
    }

    private fun setSunsetText(view: View, weather: Weather?) {
        val description = context.getString(R.string.sunset)
        val value = getFormatTime(weather!!.sunset)
        setDataToView(view, R.id.sunset_current_weather, description, value)
    }

    private fun setDataToView(view: View, identifier: Int, description: String, value: String) {
        val textView = view.findViewById<TextView>(identifier)
        if (isActualWeather) {
            textView.visibility = VISIBLE
            textView.text = convertToDeterminationPattern(description, value)
        } else {
            textView.visibility = INVISIBLE
        }
    }

    private fun convertToDeterminationPattern(param1: String, param2: String): String {
        return String.format(Locale.getDefault(), DETERMINATION_PATTERN, param1, param2)
    }

    private fun convertWindSpeed(speed: Double): Int {
        return (speed / KM_IN_MILE * ACCURACY).toInt()
    }

    private fun convertPressure(pressure: Double): Int {
        return (pressure / hPa_IN_Hg).toInt()
    }

    private fun convertPressureRising(rising: Int): String {
        return context.getString(
            if (rising == 0)
                R.string.pressure_steady
            else if (rising == 1) R.string.pressure_rising else R.string.pressure_falling
        )
    }

    private fun convertVisibility(visibility: Double): Int {
        return (visibility / KM_IN_MILE * ACCURACY).toInt()
    }

    private fun getFormatTime(date: Date): String {
        val timeFormat = WidgetProvider.chooseSystemTimeFormat(context, FULL_TIME_FORMAT_12, TIME_FORMAT_24)
        val dateFormat = SimpleDateFormat(timeFormat, Locale.getDefault())
        return dateFormat.format(date)
    }
}