package by.rudkouski.widget.util

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import by.rudkouski.widget.R
import java.util.*
import kotlin.math.roundToInt

object WeatherUtils {

    private const val WEATHER_DEGREE_FORMAT = "%1\$d%2\$s"
    private const val DETERMINATION_PATTERN = "%1\$s: %2\$s"
    const val PRESSURE_GPA_TO_MM_HG = 1.333

    fun getIconWeatherImageResource(context: Context, iconName: String, cloudCover: Double, precipitationProbability: Double): Int {
        var postFix = ""
        var preFix = ""
        if (iconName.startsWith("partly-cloudy")) {
            if (cloudCover >= 0.6) postFix = "_mostly"
            if (cloudCover < 0.4) postFix = "_less"
        }
        if (iconName.startsWith("rain")) {
            if (cloudCover < 0.6) {
                preFix = if (cloudCover < 0.4) "less_" else "mostly_"
            } else {
                postFix = if (precipitationProbability >= 0.5) "_mostly" else "_less"
            }
        }
        return context.resources.getIdentifier(preFix + iconName.replace("-", "_") + postFix, "mipmap",
            context.packageName)
    }

    fun convertDoubleToPercents(context: Context, double: Double) =
        "${mathRound(double * 100)}${context.getString(R.string.percent_unit)}"

    fun getDegreeText(context: Context, temperature: Double) =
        String.format(Locale.getDefault(), WEATHER_DEGREE_FORMAT, mathRound(temperature), context.getString(R.string.temperature_unit))

    fun mathRound(double: Double) = double.roundToInt()

    fun setDataToView(view: View, identifier: Int, description: String?, value: String?) {
        val textView = view.findViewById<TextView>(identifier)
        textView.text = if (description != null) convertToDeterminationPattern(description, value!!) else value
    }

    fun setDataToView(view: View, identifier: Int, description: Spannable, value: Spannable) {
        val textView = view.findViewById<TextView>(identifier)
        textView.text = description
        textView.append(value)
    }

    fun convertToDeterminationPattern(param1: String, param2: String) =
        String.format(Locale.getDefault(), DETERMINATION_PATTERN, param1, param2)

    fun convertWindDirection(context: Context, direction: Int): String {
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

    fun setPrecipitationText(view: View, precipitationProbability: Double, precipitationType: String?, identifier: Int) {
        val description = getSpannableStringDescription(view.context, R.string.precipitationProbability)
        val value =
            getSpannableStringValue(view.context, if (precipitationProbability > 0) "${view.context.getString(
                view.context.resources.getIdentifier(precipitationType, "string",
                    view.context.packageName))}, ${convertDoubleToPercents(view.context, precipitationProbability)}" else view.context.getString(
                R.string.no_rain))
        setDataToView(view, identifier, description, value)
    }

    fun setDewPointText(view: View, dewPoint: Double, identifier: Int) {
        val description = getSpannableStringDescription(view.context, R.string.dewPoint)
        val value =
            getSpannableStringValue(view.context, getDegreeText(view.context, dewPoint))
        setDataToView(view, identifier, description, value)
    }

    fun setHumidityText(view: View, humidity: Double, identifier: Int) {
        val description = getSpannableStringDescription(view.context, R.string.humidity)
        val value = getSpannableStringValue(view.context,
            convertDoubleToPercents(view.context, humidity))
        setDataToView(view, identifier, description, value)
    }

    fun setPressureText(view: View, pressure: Double, identifier: Int) {
        val description = getSpannableStringDescription(view.context, R.string.pressure)
        val value = getSpannableStringValue(view.context,
            "${mathRound(pressure / PRESSURE_GPA_TO_MM_HG)} ${view.context.getString(R.string.pressure_unit)}")
        setDataToView(view, identifier, description, value)
    }

    fun setWindText(view: View, windSpeed: Double, windDirection: Int, windGust: Double, identifier: Int) {
        val description = getSpannableStringDescription(view.context, R.string.wind)
        val value = getSpannableStringValue(view.context,
            if (windSpeed != 0.0) "${convertWindDirection(view.context, windDirection)}, ${mathRound(windSpeed)} ${view.context.getString(
                R.string.speed_unit)}, " + "${view.context.getString(R.string.gust)} ${mathRound(windGust)} ${view.context.getString(
                R.string.speed_unit)}" else view.context.getString(R.string.windless))
        setDataToView(view, identifier, description, value)
    }

    fun setVisibilityText(view: View, visibility: Double, identifier: Int) {
        val description = getSpannableStringDescription(view.context, R.string.visibility)
        val value = getSpannableStringValue(view.context,
            "$visibility ${view.context.getString(R.string.distance_unit)}")
        setDataToView(view, identifier, description, value)
    }

    fun setCloudCoverText(view: View, cloudCover: Double, identifier: Int) {
        val description = getSpannableStringDescription(view.context, R.string.cloud_cover)
        val value = getSpannableStringValue(view.context,
            convertDoubleToPercents(view.context, cloudCover))
        setDataToView(view, identifier, description, value)
    }

    fun setUvIndexText(view: View, uvIndex: Int, identifier: Int) {
        val description = getSpannableStringDescription(view.context, R.string.uv_index)
        val value = getSpannableStringValue(view.context, "$uvIndex")
        setDataToView(view, identifier, description, value)
    }

    fun getSpannableStringDescription(context: Context, resId: Int): Spannable {
        val description = SpannableString(context.getString(resId))
        description.setSpan(ForegroundColorSpan(getDarkTextColor(context)), 0, description.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        description.setSpan(RelativeSizeSpan(0.8f), 0, description.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return description
    }

    fun getSpannableStringValue(context: Context, res: String): Spannable {
        val value = SpannableString("\n" + res)
        value.setSpan(ForegroundColorSpan(getLightTextColor(context)), 0, value.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
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
}