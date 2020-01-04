package by.rudkouski.widget.provider

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getActivity
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.appwidget.AppWidgetManager.getInstance
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
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.RemoteViews
import by.rudkouski.widget.R
import by.rudkouski.widget.app.Constants.WIDGET_CLOCK_UPDATE_REQUEST_CODE
import by.rudkouski.widget.app.Constants.WIDGET_UPDATE_ACTION
import by.rudkouski.widget.entity.Location
import by.rudkouski.widget.entity.Location.Companion.CURRENT_LOCATION
import by.rudkouski.widget.entity.Location.Companion.CURRENT_LOCATION_ID
import by.rudkouski.widget.entity.Setting
import by.rudkouski.widget.repository.ForecastRepository.getForecastsByLocationId
import by.rudkouski.widget.repository.LocationRepository.getLocationById
import by.rudkouski.widget.repository.LocationRepository.resetCurrentLocation
import by.rudkouski.widget.repository.SettingRepository.getPrivateSettingsByWidgetId
import by.rudkouski.widget.repository.WeatherRepository.getCurrentWeatherByLocationId
import by.rudkouski.widget.repository.WidgetRepository.deleteWidgetById
import by.rudkouski.widget.repository.WidgetRepository.getWidgetById
import by.rudkouski.widget.update.receiver.LocationUpdateBroadcastReceiver.Companion.isPermissionsDenied
import by.rudkouski.widget.update.receiver.LocationUpdateBroadcastReceiver.Companion.isPermissionsGranted
import by.rudkouski.widget.update.receiver.LocationUpdateBroadcastReceiver.Companion.updateCurrentLocation
import by.rudkouski.widget.update.receiver.WeatherUpdateBroadcastReceiver.Companion.updateOtherWeathers
import by.rudkouski.widget.util.WeatherUtils.getIconWeatherImageResource
import by.rudkouski.widget.view.forecast.ForecastActivity.Companion.startForecastActivityIntent
import by.rudkouski.widget.view.location.LocationActivity.Companion.startLocationActivityIntent
import org.threeten.bp.OffsetDateTime.now
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneId.systemDefault
import org.threeten.bp.format.DateTimeFormatter
import java.util.Locale.getDefault
import kotlin.math.roundToInt

class WidgetProvider : AppWidgetProvider() {

    companion object {
        private const val DATE_WITH_DAY_SHORT_FORMAT = "EEE, dd MMM"
        private const val SYSTEM_TIME_FORMAT_24 = 24


        fun updateWidget(context: Context) {
            val intent = Intent(context, WidgetProvider::class.java)
            intent.action = WIDGET_UPDATE_ACTION
            PendingIntent.getBroadcast(context, WIDGET_CLOCK_UPDATE_REQUEST_CODE, intent, FLAG_UPDATE_CURRENT).send()
        }

        fun chooseSystemTimeFormat(context: Context, timeFormat12: String, timeFormat24: String): String {
            return if (Settings.System.getInt(context.contentResolver, TIME_12_24, 0) == SYSTEM_TIME_FORMAT_24) timeFormat24 else timeFormat12
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (WIDGET_UPDATE_ACTION == intent.action) {
            val componentName = ComponentName(context, javaClass.name)
            val widgetManager = getInstance(context)
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
        val locationId = widget?.locationId ?: 0
        val location = {
            if (CURRENT_LOCATION_ID == locationId) {
                if (isPermissionsDenied()) {
                    resetCurrentLocation()
                } else {
                    val loc = getLocationById(locationId)
                    if (CURRENT_LOCATION == loc!!.name_code) {
                        updateCurrentLocation(context)
                    }
                }
            }
            getLocationById(locationId)
        }.invoke()
        val settings = getPrivateSettingsByWidgetId(widgetId)
        val isBold = settings?.find { Setting.Code.SETTING_BOLD == it.code }?.getBooleanValue() ?: false
        updateClockAndDate(remoteViews, location?.zoneId ?: systemDefault(), isBold)
        updateLocation(remoteViews, context, location, isBold)
        updateWeather(remoteViews, context, locationId, isBold)
        setPendingIntents(remoteViews, context, widgetId, locationId)
        return remoteViews
    }

    private fun updateClockAndDate(remoteViews: RemoteViews, zoneId: ZoneId, isBold: Boolean) {
        remoteViews.setString(R.id.clock_widget, "setTimeZone", zoneId.id)
        val currentTime = now(zoneId)
        val dateFormat = currentTime.format(DateTimeFormatter.ofPattern(DATE_WITH_DAY_SHORT_FORMAT, getDefault()))
        val spanDateText = createSpannableString(dateFormat, isBold)
        remoteViews.setTextViewText(R.id.date_widget, spanDateText)
    }

    private fun createSpannableString(resource: String, isBold: Boolean): SpannableString {
        val spanString = SpannableString(resource)
        spanString.setSpan(StyleSpan(if (isBold) BOLD else NORMAL), 0, spanString.length, 0)
        return spanString
    }

    private fun updateLocation(remoteViews: RemoteViews, context: Context, location: Location?, isBold: Boolean) {
        val spanLocationText = createSpannableString("  ${location?.getName(context) ?: context.getString(R.string.default_location)}", isBold)
        remoteViews.setTextViewText(R.id.location_widget, spanLocationText)
    }

    private fun updateWeather(remoteViews: RemoteViews, context: Context, locationId: Int, isBold: Boolean) {
        val weather = getCurrentWeatherByLocationId(locationId)
        if (weather != null) {
            remoteViews.setViewVisibility(R.id.weather_widget, VISIBLE)
            remoteViews.setViewVisibility(R.id.no_data, INVISIBLE)
            remoteViews.setImageViewResource(R.id.weather_image_widget,
                getIconWeatherImageResource(context, weather.iconName, weather.cloudCover, weather.precipitationProbability))
            remoteViews.setTextViewText(R.id.degrees_widget, createSpannableString(weather.temperature.roundToInt().toString(), isBold))
            remoteViews.setTextViewText(R.id.degrees_text_widget, createSpannableString(context.getString(R.string.temperature_unit), isBold))
        } else {
            remoteViews.setViewVisibility(R.id.weather_widget, INVISIBLE)
            remoteViews.setViewVisibility(R.id.no_data, VISIBLE)
            remoteViews.setTextViewText(R.id.no_data, createSpannableString(context.getString(R.string.default_weather), isBold))
        }
    }

    private fun setPendingIntents(remoteViews: RemoteViews, context: Context, widgetId: Int, locationId: Int) {
        remoteViews.setOnClickPendingIntent(R.id.clock_widget, createClockPendingIntent(context, widgetId))
        remoteViews.setOnClickPendingIntent(R.id.date_widget, createDatePendingIntent(context, widgetId))
        remoteViews.setOnClickPendingIntent(R.id.location_widget, createLocationPendingIntent(context, widgetId))
        remoteViews.setOnClickPendingIntent(R.id.weather_widget, createForecastPendingIntent(context, widgetId))
        if (isPendingIntentEnable(locationId)) {
            remoteViews.setOnClickPendingIntent(R.id.no_data_widget, createForecastPendingIntent(context, widgetId))
        } else {
            remoteViews.setOnClickPendingIntent(R.id.no_data_widget, null)
        }
    }

    private fun isPendingIntentEnable(locationId: Int) =
        (CURRENT_LOCATION_ID != locationId || (CURRENT_LOCATION_ID == locationId && isPermissionsGranted())) &&
            (getCurrentWeatherByLocationId(locationId) != null || !getForecastsByLocationId(locationId).isNullOrEmpty())

    private fun createClockPendingIntent(context: Context, widgetId: Int): PendingIntent {
        val clockIntent = Intent(ACTION_SHOW_ALARMS)
        clockIntent.putExtra(EXTRA_APPWIDGET_ID, widgetId)
        return getActivity(context, widgetId, clockIntent, FLAG_UPDATE_CURRENT)
    }

    private fun createDatePendingIntent(context: Context, widgetId: Int): PendingIntent {
        val dateIntent = Intent(ACTION_QUICK_CLOCK)
        dateIntent.putExtra(EXTRA_APPWIDGET_ID, widgetId)
        return getActivity(context, widgetId, dateIntent, FLAG_UPDATE_CURRENT)
    }

    private fun createLocationPendingIntent(context: Context, widgetId: Int): PendingIntent {
        val locationIntent = startLocationActivityIntent(context, widgetId)
        return getActivity(context, widgetId, locationIntent, FLAG_UPDATE_CURRENT)
    }

    private fun createForecastPendingIntent(context: Context, widgetId: Int): PendingIntent {
        val forecastIntent = startForecastActivityIntent(context, widgetId)
        return getActivity(context, widgetId, forecastIntent, FLAG_UPDATE_CURRENT)
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        updateWidget(context)
        updateOtherWeathers(context)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        for (appWidgetId in appWidgetIds) {
            deleteWidgetById(appWidgetId)
        }
    }
}