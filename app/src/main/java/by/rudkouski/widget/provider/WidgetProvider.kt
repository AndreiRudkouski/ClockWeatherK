package by.rudkouski.widget.provider

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_QUICK_CLOCK
import android.graphics.Typeface.BOLD
import android.graphics.Typeface.NORMAL
import android.provider.AlarmClock.ACTION_SHOW_ALARMS
import android.provider.Settings
import android.provider.Settings.System.TIME_12_24
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.View
import android.widget.RemoteViews
import by.rudkouski.widget.R
import by.rudkouski.widget.entity.Location
import by.rudkouski.widget.repository.LocationRepository.getLocationById
import by.rudkouski.widget.repository.WeatherRepository.getCurrentWeatherByLocationId
import by.rudkouski.widget.repository.WidgetRepository.deleteWidgetById
import by.rudkouski.widget.repository.WidgetRepository.getWidgetById
import by.rudkouski.widget.update.listener.LocationChangeListener
import by.rudkouski.widget.update.receiver.WeatherUpdateBroadcastReceiver
import by.rudkouski.widget.update.scheduler.UpdateWeatherScheduler
import by.rudkouski.widget.util.WeatherUtils
import by.rudkouski.widget.view.forecast.ForecastActivity
import by.rudkouski.widget.view.location.LocationActivity
import org.threeten.bp.OffsetDateTime.now
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

class WidgetProvider : AppWidgetProvider() {

    companion object {
        private const val TIME_FORMAT_12 = "h:mm"
        private const val TIME_FORMAT_24 = "H:mm"
        private const val DATE_WITH_DAY_SHORT_FORMAT = "EEE, dd MMM"
        private const val WIDGET_CLOCK_UPDATE_REQUEST_CODE = 1001
        private const val SYSTEM_TIME_FORMAT_24 = 24

        private val widgetUpdateAction = "${WidgetProvider::class.java.`package`}.WIDGET_UPDATE"

        fun updateWidget(context: Context) {
            val intent = Intent(context, WidgetProvider::class.java)
            intent.action = widgetUpdateAction
            PendingIntent.getBroadcast(context, WIDGET_CLOCK_UPDATE_REQUEST_CODE, intent, FLAG_UPDATE_CURRENT).send()
        }

        fun chooseSystemTimeFormat(context: Context, timeFormat12: String, timeFormat24: String): String {
            return if (Settings.System.getInt(context.contentResolver, TIME_12_24, 0) == SYSTEM_TIME_FORMAT_24) timeFormat24 else timeFormat12
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (widgetUpdateAction == intent.action) {
            val componentName = ComponentName(context, javaClass.name)
            val widgetManager = AppWidgetManager.getInstance(context)
            val widgetIds = widgetManager.getAppWidgetIds(componentName)
            for (widgetId in widgetIds) {
                val remoteViews = updateWidget(context, widgetId)
                widgetManager.updateAppWidget(widgetId, remoteViews)
            }
        }
    }

    private fun updateWidget(context: Context, widgetId: Int): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.widget)
        val widget = getWidgetById(widgetId)
        if (widget != null) {
            val location = getLocationById(widget.locationId)
            updateClockAndDate(remoteViews, context, location.zoneId, widget.isBold)
            updateLocation(remoteViews, context, location, widget.isBold)
            updateWeather(remoteViews, context, location.id, widget.isBold)
            setPendingIntents(remoteViews, context, widgetId)
        }
        return remoteViews
    }

    private fun updateClockAndDate(remoteViews: RemoteViews, context: Context, zoneId: ZoneId, isBold: Boolean) {
        val currentTime = now(zoneId)
        val timeFormat =
            currentTime.format(DateTimeFormatter.ofPattern(chooseSystemTimeFormat(context, TIME_FORMAT_12, TIME_FORMAT_24), Locale.getDefault()))
        remoteViews.setTextViewText(R.id.clock_widget, timeFormat)
        val dateFormat = currentTime.format(DateTimeFormatter.ofPattern(DATE_WITH_DAY_SHORT_FORMAT, Locale.getDefault()))
        val spanDateText = createSpannableString(dateFormat, isBold)
        remoteViews.setTextViewText(R.id.date_widget, spanDateText)
    }

    private fun createSpannableString(resource: String, isBold: Boolean): SpannableString {
        val spanString = SpannableString(resource)
        spanString.setSpan(StyleSpan(if (isBold) BOLD else NORMAL), 0, spanString.length, 0)
        return spanString
    }

    private fun updateLocation(remoteViews: RemoteViews, context: Context, location: Location, isBold: Boolean) {
        val spanLocationText = createSpannableString("  ${location.getName(context)}", isBold)
        remoteViews.setTextViewText(R.id.location_widget, spanLocationText)
    }

    private fun updateWeather(remoteViews: RemoteViews, context: Context, locationId: Int, isBold: Boolean) {
        val weather = getCurrentWeatherByLocationId(locationId)
        if (weather != null) {
            remoteViews.setViewVisibility(R.id.weather_widget, View.VISIBLE)
            remoteViews.setImageViewResource(R.id.weather_image_widget,
                WeatherUtils.getIconWeatherImageResource(context, weather.iconName, weather.cloudCover, weather.precipitationProbability))
            remoteViews.setTextViewText(R.id.degrees_widget, createSpannableString(weather.temperature.roundToInt().toString(), isBold))
            remoteViews.setTextViewText(R.id.degrees_text_widget, createSpannableString(context.getString(R.string.temperature_unit), isBold))
        } else {
            remoteViews.setViewVisibility(R.id.weather_widget, View.INVISIBLE)
        }
    }

    private fun setPendingIntents(remoteViews: RemoteViews, context: Context, widgetId: Int) {
        remoteViews.setOnClickPendingIntent(R.id.clock_widget, createClockPendingIntent(context, widgetId))
        remoteViews.setOnClickPendingIntent(R.id.date_widget, createDatePendingIntent(context, widgetId))
        remoteViews.setOnClickPendingIntent(R.id.location_widget, createLocationPendingIntent(context, widgetId))
        remoteViews.setOnClickPendingIntent(R.id.widget, createForecastPendingIntent(context, widgetId))
    }

    private fun createClockPendingIntent(context: Context, widgetId: Int): PendingIntent {
        val clockIntent = Intent(ACTION_SHOW_ALARMS)
        clockIntent.putExtra(EXTRA_APPWIDGET_ID, widgetId)
        return PendingIntent.getActivity(context, widgetId, clockIntent, FLAG_UPDATE_CURRENT)
    }

    private fun createDatePendingIntent(context: Context, widgetId: Int): PendingIntent {
        val dateIntent = Intent(ACTION_QUICK_CLOCK)
        dateIntent.putExtra(EXTRA_APPWIDGET_ID, widgetId)
        return PendingIntent.getActivity(context, widgetId, dateIntent, FLAG_UPDATE_CURRENT)
    }

    private fun createLocationPendingIntent(context: Context, widgetId: Int): PendingIntent {
        val locationIntent = LocationActivity.startIntent(context, widgetId)
        return PendingIntent.getActivity(context, widgetId, locationIntent, FLAG_UPDATE_CURRENT)
    }

    private fun createForecastPendingIntent(context: Context, widgetId: Int): PendingIntent {
        val forecastIntent = ForecastActivity.startIntent(context, widgetId)
        return PendingIntent.getActivity(context, widgetId, forecastIntent, FLAG_UPDATE_CURRENT)
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        UpdateWeatherScheduler.startUpdateWeatherScheduler()
        LocationChangeListener.startLocationUpdate()
        updateWidget(context)
        WeatherUpdateBroadcastReceiver.updateAllWeathers(context)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        for (appWidgetId in appWidgetIds) {
            deleteWidgetById(appWidgetId)
        }
    }
}