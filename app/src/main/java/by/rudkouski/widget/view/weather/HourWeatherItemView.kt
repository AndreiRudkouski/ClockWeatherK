package by.rudkouski.widget.view.weather

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import by.rudkouski.widget.R
import by.rudkouski.widget.entity.Weather
import by.rudkouski.widget.provider.WidgetProvider.Companion.chooseSystemTimeFormat
import by.rudkouski.widget.util.WeatherUtils
import by.rudkouski.widget.util.WeatherUtils.getDegreeText
import by.rudkouski.widget.view.weather.WeatherItemView.Companion.TIME_FORMAT_24
import org.threeten.bp.format.DateTimeFormatter.ofPattern
import java.util.*

class HourWeatherItemView : LinearLayout, View.OnClickListener {

    private var widgetId = 0
    private lateinit var weather: Weather

    companion object {
        const val FULL_TIME_FORMAT_12_IN_TWO_LINE = "h:mm\na"
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun updateHourWeatherItemView(widgetId: Int, weather: Weather) {
        this.widgetId = widgetId
        this.weather = weather
        val view = findViewById<View>(R.id.forecast_hour_weather)
        setTime(view, weather)
        setImage(view, weather)
        setDegree(view, weather)
        view.setOnClickListener(this)
    }

    private fun setTime(view: View, weather: Weather) {
        val timeTextView = view.findViewById<TextView>(R.id.time_hour_weather)
        timeTextView.text =
            weather.date.format(ofPattern(chooseSystemTimeFormat(context, FULL_TIME_FORMAT_12_IN_TWO_LINE, TIME_FORMAT_24), Locale.getDefault()))
    }

    private fun setImage(view: View, weather: Weather) {
        val imageView = view.findViewById<ImageView>(R.id.hour_weather_image)
        imageView.setImageResource(
            WeatherUtils.getIconWeatherImageResource(context, weather.iconName, weather.cloudCover, weather.precipitationProbability))
    }

    private fun setDegree(view: View, weather: Weather) {
        val degreeTextView = view.findViewById<TextView>(R.id.degrees_hour_weather)
        degreeTextView.text = getDegreeText(context, weather.temperature)
    }

    override fun onClick(view: View) {
        HourWeatherActivity.start(context, widgetId, weather.id)
    }
}