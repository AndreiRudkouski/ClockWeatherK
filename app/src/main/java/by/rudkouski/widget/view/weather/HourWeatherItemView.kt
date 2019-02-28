package by.rudkouski.widget.view.weather

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import by.rudkouski.widget.R
import by.rudkouski.widget.entity.Weather
import by.rudkouski.widget.provider.WidgetProvider
import by.rudkouski.widget.view.weather.WeatherUtils.getDegreeText
import java.text.SimpleDateFormat
import java.util.*

class HourWeatherItemView : LinearLayout {

    private var isActualWeather: Boolean = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun updateHourWeatherItemView(weather: Weather) {
        val view = findViewById<View>(R.id.current_hour_weather)
        isActualWeather = WidgetProvider.isActualWeather(weather)
        if (isActualWeather) {
            setTime(view, weather)
            setImage(view, weather)
            setDegree(view, weather)
        }
    }

    private fun setTime(view: View, weather: Weather) {
        val timeTextView = view.findViewById<TextView>(R.id.time_hour_weather)
        val timeFormat = SimpleDateFormat(
            WidgetProvider.chooseSystemTimeFormat(context, WeatherItemView.FULL_TIME_FORMAT_12,
                WeatherItemView.TIME_FORMAT_24), Locale.getDefault())
        timeFormat.timeZone = weather.date.timeZone
        timeTextView.text = timeFormat.format(weather.date.time)
    }

    private fun setImage(view: View, weather: Weather) {
        val imageView = view.findViewById<ImageView>(R.id.hour_weather_image)
        imageView.setImageResource(WeatherUtils.getWeatherImageResource(context, weather))
    }

    private fun setDegree(view: View, weather: Weather) {
        val degreeTextView = view.findViewById<TextView>(R.id.degrees_hour_weather)
        degreeTextView.text = getDegreeText(weather.temperature)
    }
}