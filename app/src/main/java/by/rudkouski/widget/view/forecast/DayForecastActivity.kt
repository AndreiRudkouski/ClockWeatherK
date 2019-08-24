package by.rudkouski.widget.view.forecast

import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.TypedValue
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
import by.rudkouski.widget.view.weather.WeatherUtils.convertDoubleToPercents
import by.rudkouski.widget.view.weather.WeatherUtils.convertWindDirection
import by.rudkouski.widget.view.weather.WeatherUtils.getDegreeText
import by.rudkouski.widget.view.weather.WeatherUtils.mathRound
import by.rudkouski.widget.view.weather.WeatherUtils.setDataToView
import java.text.SimpleDateFormat
import java.util.*


class DayForecastActivity : BaseActivity() {

    companion object {
        const val EXTRA_FORECAST_ID = "forecastId"

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
        setContentView(R.layout.day_forecast_activity)
        updateActivity()
    }

    private fun updateActivity() {
        val forecast = getForecastById(forecastId)
        val view = findViewById<View>(R.id.day_forecast)
        setDescriptionText(view, forecast)
        if (forecast != null) {
            updateTitle(forecast)
            view.visibility = LinearLayout.VISIBLE
            setImage(view, forecast)
            setDegreeText(view, forecast)
            setSunriseTime(view, forecast)
            setSunsetTime(view, forecast)
            setPrecipitationText(view, forecast)
            setFeelText(view, forecast)
            setDewPointText(view, forecast)
            setHumidityText(view, forecast)
            setPressureText(view, forecast)
            setWindText(view, forecast)
            setVisibilityText(view, forecast)
            setCloudCoverText(view, forecast)
            setUvIndexText(view, forecast)
        } else {
            view.visibility = LinearLayout.INVISIBLE
            onStop()
        }
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

    private fun setDescriptionText(view: View, forecast: Forecast?) {
        val descriptionTextView = view.findViewById<TextView>(R.id.description_day_forecast)
        descriptionTextView.text = forecast?.description?.toLowerCase()?.capitalize() ?: this.getString(R.string.default_weather)
    }

    private fun setDegreeText(view: View, forecast: Forecast) {
        val degreeTextView = view.findViewById<TextView>(R.id.degrees_day_forecast)
        degreeTextView.text = String.format(Locale.getDefault(), ForecastItemView.FORECAST_DEGREE_FORMAT,
            getDegreeText(forecast.temperatureHigh), getDegreeText(forecast.temperatureLow))
    }

    private fun setSunriseTime(view: View, forecast: Forecast) {
        val description = getSpannableStringDescription(R.string.sunrise)
        val value = getSpannableStringValue(getFormatTime(forecast.sunriseTime))
        setDataToView(view, R.id.sunrise_day_forecast, description, value)
    }

    private fun setSunsetTime(view: View, forecast: Forecast) {
        val description = getSpannableStringDescription(R.string.sunset)
        val value = getSpannableStringValue(getFormatTime(forecast.sunsetTime))
        setDataToView(view, R.id.sunset_day_forecast, description, value)
    }

    private fun getFormatTime(date: Calendar): String {
        val timeFormat = WidgetProvider.chooseSystemTimeFormat(this, FULL_TIME_FORMAT_12, TIME_FORMAT_24)
        val dateFormat = SimpleDateFormat(timeFormat, Locale.getDefault())
        dateFormat.timeZone = date.timeZone
        return dateFormat.format(date.time)
    }

    private fun setPrecipitationText(view: View, forecast: Forecast) {
        val description = getSpannableStringDescription(R.string.precipitationProbability)
        val value = getSpannableStringValue(if (forecast.precipitationProbability > 0) "${getString(
            resources.getIdentifier(forecast.precipitationType, "string", packageName))}, ${convertDoubleToPercents(
            forecast.precipitationProbability)}" else getString(R.string.no_rain))
        setDataToView(view, R.id.precipitation_day_forecast, description, value)
    }

    private fun setFeelText(view: View, forecast: Forecast) {
        val description = getSpannableStringDescription(R.string.feel)
        val value = getSpannableStringValue(String.format(Locale.getDefault(), ForecastItemView.FORECAST_DEGREE_FORMAT,
            getDegreeText(forecast.apparentTemperatureHigh), getDegreeText(forecast.apparentTemperatureLow)))
        setDataToView(view, R.id.feel_day_forecast, description, value)
    }

    private fun setDewPointText(view: View, forecast: Forecast) {
        val description = getSpannableStringDescription(R.string.dewPoint)
        val value = getSpannableStringValue(getDegreeText(forecast.dewPoint))
        setDataToView(view, R.id.dew_point_day_forecast, description, value)
    }

    private fun setHumidityText(view: View, forecast: Forecast) {
        val description = getSpannableStringDescription(R.string.humidity)
        val value = getSpannableStringValue(convertDoubleToPercents(forecast.humidity))
        setDataToView(view, R.id.humidity_day_forecast, description, value)
    }

    private fun setPressureText(view: View, forecast: Forecast) {
        val description = getSpannableStringDescription(R.string.pressure)
        val value = getSpannableStringValue("${mathRound(forecast.pressure)} ${getString(R.string.pressure_unit)}")
        setDataToView(view, R.id.pressure_day_forecast, description, value)
    }

    private fun setWindText(view: View, forecast: Forecast) {
        val description = getSpannableStringDescription(R.string.wind)
        val value = getSpannableStringValue(if (forecast.windSpeed != 0.0) "${convertWindDirection(
            forecast.windDirection)}, ${mathRound(forecast.windSpeed)} ${getString(R.string.speed_unit)}, " +
            "${getString(R.string.gust)} ${mathRound(forecast.windGust)} ${getString(R.string.speed_unit)}" else getString(R.string.windless))
        setDataToView(view, R.id.wind_day_forecast, description, value)
    }

    private fun setVisibilityText(view: View, forecast: Forecast) {
        val description = getSpannableStringDescription(R.string.visibility)
        val value = getSpannableStringValue("${forecast.visibility} ${getString(R.string.distance_unit)}")
        setDataToView(view, R.id.visibility_day_forecast, description, value)
    }

    private fun setCloudCoverText(view: View, forecast: Forecast) {
        val description = getSpannableStringDescription(R.string.cloud_cover)
        val value = getSpannableStringValue(convertDoubleToPercents(forecast.cloudCover))
        setDataToView(view, R.id.cloud_cover_day_forecast, description, value)
    }

    private fun setUvIndexText(view: View, forecast: Forecast) {
        val description = getSpannableStringDescription(R.string.uv_index)
        val value = getSpannableStringValue("${forecast.uvIndex}")
        setDataToView(view, R.id.uv_index_day_forecast, description, value)
    }

    private fun getSpannableStringDescription(resId: Int): Spannable {
        val description = SpannableString(getString(resId))
        description.setSpan(ForegroundColorSpan(getDarkTextColor(this)), 0, description.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        description.setSpan(RelativeSizeSpan(0.8f), 0, description.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return description
    }

    private fun getSpannableStringValue(res: String): Spannable {
        val value = SpannableString("\n" + res)
        value.setSpan(ForegroundColorSpan(getLightTextColor(this)), 0, value.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return value
    }

    private fun getLightTextColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorTextMain, typedValue, true)
        return typedValue.data
    }

    private fun getDarkTextColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorTextAdd, typedValue, true)
        return typedValue.data
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