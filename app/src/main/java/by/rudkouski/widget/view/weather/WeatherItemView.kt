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
import by.rudkouski.widget.view.weather.WeatherUtils.convertDoubleToPercents
import by.rudkouski.widget.view.weather.WeatherUtils.convertToDeterminationPattern
import by.rudkouski.widget.view.weather.WeatherUtils.convertWindDirection
import by.rudkouski.widget.view.weather.WeatherUtils.getDegreeText
import by.rudkouski.widget.view.weather.WeatherUtils.getIconWeatherImageResource
import by.rudkouski.widget.view.weather.WeatherUtils.mathRound
import by.rudkouski.widget.view.weather.WeatherUtils.setDataToView
import java.text.SimpleDateFormat
import java.util.*

class WeatherItemView : LinearLayout {

    private var isActualWeather: Boolean = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    companion object {
        const val TIME_FORMAT_24 = "H:mm"
        const val FULL_TIME_FORMAT_12 = "h:mm a"

        private const val ENUMERATION_PATTERN = "%1\$s, %2\$s"
        private const val DATE_FORMAT_WITHOUT_YEAR = "dd MMM"
        private const val NOT_UPDATED = " -- "
    }

    fun updateWeatherItemView(weather: Weather?) {
        val view = findViewById<View>(R.id.current_weather)
        isActualWeather = WidgetProvider.isActualWeather(weather)
        setDescriptionText(view, weather)
        setUpdateDateText(view, weather)
        if (isActualWeather) {
            setImage(view, weather)
            setDegreeText(view, weather)
            setPrecipitationText(view, weather)
            setFeelText(view, weather)
            setDewPointText(view, weather)
            setHumidityText(view, weather)
            setPressureText(view, weather)
            setWindText(view, weather)
            setVisibilityText(view, weather)
            setCloudCoverText(view, weather)
            setUvIndexText(view, weather)
        }
    }

    private fun setImage(view: View, weather: Weather?) {
        val imageView = view.findViewById<ImageView>(R.id.image_current_weather)
        imageView.setImageResource(getIconWeatherImageResource(context, weather!!.iconName, weather.cloudCover, weather.precipitationProbability))
    }

    private fun setDegreeText(view: View, weather: Weather?) {
        val degreeTextView = view.findViewById<TextView>(R.id.degrees_current_weather)
        degreeTextView.text = getDegreeText(weather!!.temperature)
    }

    private fun setDescriptionText(view: View, weather: Weather?) {
        val descriptionTextView = view.findViewById<TextView>(R.id.description_current_weather)
        descriptionTextView.text = if (isActualWeather) weather!!.description.toLowerCase().capitalize() else
            context.getString(R.string.default_weather)
    }

    private fun setUpdateDateText(view: View, weather: Weather?) {
        val updateDateTextView = view.findViewById<TextView>(R.id.update_date_current_weather)
        updateDateTextView.visibility = VISIBLE
        val timeFormat = WidgetProvider.chooseSystemTimeFormat(context, FULL_TIME_FORMAT_12, TIME_FORMAT_24)
        val dateWithTimeFormat = convertToEnumerationPattern(DATE_FORMAT_WITHOUT_YEAR, timeFormat)
        val dateFormat = SimpleDateFormat(dateWithTimeFormat, Locale.getDefault())
        val dateText = if (weather != null) dateFormat.format(weather.date.time) else NOT_UPDATED
        updateDateTextView.text = convertToDeterminationPattern(context.getString(R.string.update_date), dateText)
    }

    private fun setPrecipitationText(view: View, weather: Weather?) {
        val description = context.getString(R.string.precipitationProbability)
        val value = if (weather!!.precipitationProbability > 0) "${view.context.getString(
            view.context.resources.getIdentifier(weather.precipitationType, "string", view.context.packageName))}, ${convertDoubleToPercents(
            weather.precipitationProbability)}" else view.context.getString(R.string.no_rain)
        setDataToView(view, R.id.precipitation_current_weather, description, value)
    }

    private fun setFeelText(view: View, weather: Weather?) {
        val description = context.getString(R.string.feel)
        val value = getDegreeText(weather!!.apparentTemperature)
        setDataToView(view, R.id.feel_current_weather, description, value)
    }

    private fun setDewPointText(view: View, weather: Weather?) {
        val description = context.getString(R.string.dewPoint)
        val value = getDegreeText(weather!!.dewPoint)
        setDataToView(view, R.id.dew_point_current_weather, description, value)
    }

    private fun setHumidityText(view: View, weather: Weather?) {
        val description = context.getString(R.string.humidity)
        val value = convertDoubleToPercents(weather!!.humidity)
        setDataToView(view, R.id.humidity_current_weather, description, value)
    }

    private fun setPressureText(view: View, weather: Weather?) {
        val description = context.getString(R.string.pressure)
        val value = "${mathRound(weather!!.pressure)} ${context.getString(
            R.string.pressure_unit)}"
        setDataToView(view, R.id.pressure_current_weather, description, value)
    }

    private fun setWindText(view: View, weather: Weather?) {
        val description = context.getString(R.string.wind)
        val value = if (weather!!.windSpeed != 0.0) "${convertWindDirection(
            weather.windDirection)}, ${mathRound(weather.windSpeed)} ${context.getString(
            R.string.speed_unit)}, ${context.getString(R.string.gust)} ${mathRound(
            weather.windGust)} ${context.getString(R.string.speed_unit)}" else context.getString(
            R.string.windless)
        setDataToView(view, R.id.wind_current_weather, description, value)
    }

    private fun setVisibilityText(view: View, weather: Weather?) {
        val description = context.getString(R.string.visibility)
        val value = "${weather!!.visibility} ${context.getString(
            R.string.distance_unit)}"
        setDataToView(view, R.id.visibility_current_weather, description, value)
    }

    private fun setCloudCoverText(view: View, weather: Weather?) {
        val description = context.getString(R.string.cloud_cover)
        val value = convertDoubleToPercents(weather!!.cloudCover)
        setDataToView(view, R.id.cloud_cover_current_weather, description, value)
    }

    private fun setUvIndexText(view: View, weather: Weather?) {
        val description = context.getString(R.string.uv_index)
        val value = "${weather!!.uvIndex}"
        setDataToView(view, R.id.uv_index_current_weather, description, value)
    }

    private fun convertToEnumerationPattern(param1: String, param2: String) =
        String.format(Locale.getDefault(), ENUMERATION_PATTERN, param1, param2)
}