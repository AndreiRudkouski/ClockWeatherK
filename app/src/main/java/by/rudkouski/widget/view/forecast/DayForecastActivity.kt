package by.rudkouski.widget.view.forecast

import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import by.rudkouski.widget.R
import by.rudkouski.widget.entity.Forecast
import by.rudkouski.widget.provider.WidgetProvider
import by.rudkouski.widget.repository.ForecastRepository.getForecastById
import by.rudkouski.widget.view.BaseActivity
import by.rudkouski.widget.view.forecast.ForecastItemView.Companion.DATE_WITH_DAY_SHORT_FORMAT
import by.rudkouski.widget.view.weather.WeatherItemView.Companion.FULL_TIME_FORMAT_12
import by.rudkouski.widget.view.weather.WeatherItemView.Companion.TIME_FORMAT_24
import by.rudkouski.widget.view.weather.WeatherUtils
import by.rudkouski.widget.view.weather.WeatherUtils.getDegreeText
import by.rudkouski.widget.view.weather.WeatherUtils.getSpannableStringDescription
import by.rudkouski.widget.view.weather.WeatherUtils.getSpannableStringValue
import by.rudkouski.widget.view.weather.WeatherUtils.setCloudCoverText
import by.rudkouski.widget.view.weather.WeatherUtils.setDataToView
import by.rudkouski.widget.view.weather.WeatherUtils.setDewPointText
import by.rudkouski.widget.view.weather.WeatherUtils.setHumidityText
import by.rudkouski.widget.view.weather.WeatherUtils.setPrecipitationText
import by.rudkouski.widget.view.weather.WeatherUtils.setPressureText
import by.rudkouski.widget.view.weather.WeatherUtils.setUvIndexText
import by.rudkouski.widget.view.weather.WeatherUtils.setVisibilityText
import by.rudkouski.widget.view.weather.WeatherUtils.setWindText
import java.text.SimpleDateFormat
import java.util.*


class DayForecastActivity : BaseActivity() {

    companion object {
        private const val EXTRA_FORECAST_ID = "forecastId"

        fun start(context: Context, widgetId: Int, forecastId: Int) {
            val intent = Intent(context, DayForecastActivity::class.java)
            intent.putExtra(EXTRA_APPWIDGET_ID, widgetId)
            intent.putExtra(EXTRA_FORECAST_ID, forecastId)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val forecastId = intent?.extras?.getInt(EXTRA_FORECAST_ID) ?: 0
        setContentView(R.layout.day_forecast_activity)
        updateActivity(forecastId)
    }

    private fun updateActivity(forecastId: Int) {
        val forecast = getForecastById(forecastId)
        val view = findViewById<View>(R.id.day_forecast)
        setDescriptionText(view, forecast)
        if (forecast != null) {
            view.visibility = LinearLayout.VISIBLE
            updateTitle(forecast)
            setImage(view, forecast)
            setDegreeText(view, forecast)
            setSunriseTime(view, forecast)
            setSunsetTime(view, forecast)
            setPrecipitationText(view, forecast.precipitationProbability, R.id.precipitation_day_forecast)
            setFeelText(view, forecast)
            setHumidityText(view, forecast.humidity, R.id.humidity_day_forecast)
            setPressureText(view, forecast.pressure, R.id.pressure_day_forecast)
            setWindText(view, forecast.windSpeed, forecast.windDirection, forecast.windGust, R.id.wind_day_forecast)
            setVisibilityText(view, forecast.visibility, R.id.visibility_day_forecast)
            setCloudCoverText(view, forecast.cloudCover, R.id.cloud_cover_day_forecast)
            setDewPointText(view, forecast.dewPoint, R.id.dew_point_day_forecast)
            setUvIndexText(view, forecast.uvIndex, R.id.uv_index_day_forecast)
        } else {
            view.visibility = LinearLayout.INVISIBLE
            onStop()
        }
    }

    private fun setDescriptionText(view: View, forecast: Forecast?) {
        val descriptionTextView = view.findViewById<TextView>(R.id.description_day_forecast)
        descriptionTextView.text = forecast?.description?.toLowerCase()?.capitalize() ?: this.getString(R.string.default_weather)
    }

    private fun updateTitle(forecast: Forecast) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar_day_forecast)
        val dateFormat = SimpleDateFormat(DATE_WITH_DAY_SHORT_FORMAT, Locale.getDefault())
        toolbar.title = dateFormat.format(forecast.date.time)
        setSupportActionBar(toolbar)
    }

    private fun setImage(view: View, forecast: Forecast) {
        val imageView = view.findViewById<ImageView>(R.id.image_day_forecast)
        imageView.setImageResource(
            WeatherUtils.getIconWeatherImageResource(this, forecast.iconName, forecast.cloudCover, forecast.precipitationProbability))
    }

    private fun setDegreeText(view: View, forecast: Forecast) {
        val degreeTextView = view.findViewById<TextView>(R.id.degrees_day_forecast)
        degreeTextView.text = String.format(Locale.getDefault(), ForecastItemView.FORECAST_DEGREE_FORMAT,
            getDegreeText(forecast.temperatureHigh), getDegreeText(forecast.temperatureLow))
    }

    private fun setSunriseTime(view: View, forecast: Forecast) {
        val description = getSpannableStringDescription(this, R.string.sunrise)
        val value = getSpannableStringValue(this, getFormatTime(forecast.sunriseTime))
        setDataToView(view, R.id.sunrise_day_forecast, description, value)
    }

    private fun setSunsetTime(view: View, forecast: Forecast) {
        val description = getSpannableStringDescription(this, R.string.sunset)
        val value = getSpannableStringValue(this, getFormatTime(forecast.sunsetTime))
        setDataToView(view, R.id.sunset_day_forecast, description, value)
    }

    private fun getFormatTime(date: Calendar): String {
        val timeFormat = WidgetProvider.chooseSystemTimeFormat(this, FULL_TIME_FORMAT_12, TIME_FORMAT_24)
        val dateFormat = SimpleDateFormat(timeFormat, Locale.getDefault())
        dateFormat.timeZone = date.timeZone
        return dateFormat.format(date.time)
    }

    private fun setFeelText(view: View, forecast: Forecast) {
        val description = getSpannableStringDescription(this, R.string.feel)
        val value = getSpannableStringValue(this, String.format(Locale.getDefault(), ForecastItemView.FORECAST_DEGREE_FORMAT,
            getDegreeText(forecast.apparentTemperatureHigh), getDegreeText(forecast.apparentTemperatureLow)))
        setDataToView(view, R.id.feel_day_forecast, description, value)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            callPreviousActivity()
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun callPreviousActivity() {
        val intent = ForecastActivity.startIntent(this, widgetId)
        startActivity(intent)
    }

    override fun onStop() {
        super.onStop()
        finish()
    }
}