package by.rudkouski.clockWeatherK.view.forecast

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import by.rudkouski.clockWeatherK.R
import by.rudkouski.clockWeatherK.entity.Forecast
import by.rudkouski.clockWeatherK.view.weather.WeatherCode
import java.text.SimpleDateFormat
import java.util.*

class ForecastItemView : LinearLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    companion object {
        private const val DATE_WITH_DAY_SHORT_FORMAT = "EEE, dd MMM"
        private const val FORECAST_DEGREE_FORMAT = "%1\$d%3\$s / %2\$d%3\$s"
    }

    fun updateForecastItemView(forecast: Forecast) {
        val view = findViewById<View>(R.id.forecast_item)
        setImage(view, forecast)
        setDateText(view, forecast)
        setDescriptionText(view, forecast)
        setDegreeText(view, forecast)
    }

    private fun setImage(view: View, forecast: Forecast) {
        val imageView = view.findViewById<ImageView>(R.id.weather_image_forecast)
        imageView.setImageResource(WeatherCode.getWeatherImageResourceIdByCode(context, forecast.code))
    }

    private fun setDateText(view: View, forecast: Forecast) {
        val dateTextView = view.findViewById<TextView>(R.id.date_forecast)
        val dateFormat = SimpleDateFormat(DATE_WITH_DAY_SHORT_FORMAT, Locale.getDefault())
        dateTextView.text = dateFormat.format(forecast.date.time)
    }

    private fun setDescriptionText(view: View, forecast: Forecast) {
        val descriptionTextView = view.findViewById<TextView>(R.id.description_forecast)
        descriptionTextView.text = WeatherCode.getWeatherDescriptionByCode(context, forecast.code)
    }

    private fun setDegreeText(view: View, forecast: Forecast) {
        val degreeTextView = view.findViewById<TextView>(R.id.degrees_forecast)
        val degreeText =
            String.format(Locale.getDefault(), FORECAST_DEGREE_FORMAT, forecast.highTemp, forecast.lowTemp,
                context.getString(R.string.temperature_unit))
        degreeTextView.text = degreeText
    }
}