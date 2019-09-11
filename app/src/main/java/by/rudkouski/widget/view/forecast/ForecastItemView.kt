package by.rudkouski.widget.view.forecast

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import by.rudkouski.widget.R
import by.rudkouski.widget.entity.Forecast
import by.rudkouski.widget.util.WeatherUtils
import by.rudkouski.widget.util.WeatherUtils.getDegreeText
import by.rudkouski.widget.view.forecast.DayForecastActivity.Companion.startDayForecastActivity
import org.threeten.bp.format.DateTimeFormatter.ofPattern
import java.util.*

class ForecastItemView : LinearLayout, View.OnClickListener {

    private var widgetId = 0
    private lateinit var forecast: Forecast

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    companion object {
        const val DATE_WITH_DAY_SHORT_FORMAT = "EEE, dd MMM"
        const val FORECAST_DEGREE_FORMAT = "%1\$s / %2\$s"
    }

    fun updateForecastItemView(widgetId: Int, forecast: Forecast) {
        this.widgetId = widgetId
        this.forecast = forecast
        val view = findViewById<View>(R.id.forecast_item)
        setImage(view, forecast)
        setDateText(view, forecast)
        setDescriptionText(view, forecast)
        setDegreeText(view, forecast)
        view.setOnClickListener(this)
    }

    private fun setImage(view: View, forecast: Forecast) {
        val imageView = view.findViewById<ImageView>(R.id.weather_image_forecast)
        imageView.setImageResource(
            WeatherUtils.getIconWeatherImageResource(context, forecast.iconName, forecast.cloudCover, forecast.precipitationProbability))
    }

    private fun setDateText(view: View, forecast: Forecast) {
        val dateTextView = view.findViewById<TextView>(R.id.date_forecast)
        dateTextView.text = forecast.date.format(ofPattern(DATE_WITH_DAY_SHORT_FORMAT, Locale.getDefault()))
    }

    private fun setDescriptionText(view: View, forecast: Forecast) {
        val descriptionTextView = view.findViewById<TextView>(R.id.description_forecast)
        descriptionTextView.text = forecast.description
    }

    private fun setDegreeText(view: View, forecast: Forecast) {
        val degreeTextView = view.findViewById<TextView>(R.id.degrees_forecast)
        degreeTextView.text = String.format(Locale.getDefault(), FORECAST_DEGREE_FORMAT,
            getDegreeText(context, forecast.temperatureHigh), getDegreeText(context, forecast.temperatureLow))
    }

    override fun onClick(view: View) {
        startDayForecastActivity(context, widgetId, forecast.id)
    }
}