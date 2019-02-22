package by.rudkouski.widget.view.forecast

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import by.rudkouski.widget.R
import by.rudkouski.widget.app.App
import by.rudkouski.widget.entity.Forecast
import by.rudkouski.widget.view.weather.WeatherUtils
import by.rudkouski.widget.view.weather.WeatherUtils.getDegreeText
import java.text.SimpleDateFormat
import java.util.*

class ForecastItemView : LinearLayout, View.OnClickListener {

    private lateinit var forecast: Forecast

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    companion object {
        const val DATE_WITH_DAY_SHORT_FORMAT = "EEE, dd MMM"
        const val FORECAST_DEGREE_FORMAT = "%1\$s / %2\$s"
    }

    fun updateForecastItemView(forecast: Forecast) {
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
        imageView.setImageResource(WeatherUtils.getWeatherImageResource(context, forecast))
    }

    private fun setDateText(view: View, forecast: Forecast) {
        val dateTextView = view.findViewById<TextView>(R.id.date_forecast)
        val dateFormat = SimpleDateFormat(DATE_WITH_DAY_SHORT_FORMAT, Locale.getDefault())
        dateTextView.text = dateFormat.format(forecast.date.time)
    }

    private fun setDescriptionText(view: View, forecast: Forecast) {
        val descriptionTextView = view.findViewById<TextView>(R.id.description_forecast)
        descriptionTextView.text = forecast.description
    }

    private fun setDegreeText(view: View, forecast: Forecast) {
        val degreeTextView = view.findViewById<TextView>(R.id.degrees_forecast)
        degreeTextView.text = String.format(Locale.getDefault(), FORECAST_DEGREE_FORMAT,
            getDegreeText(forecast.temperatureHigh), getDegreeText(forecast.temperatureLow))
    }

    override fun onClick(v: View) {
        DayForecastActivity.start(App.appContext, forecast.id)
    }
}