package by.rudkouski.widget.provider

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Context.DISPLAY_SERVICE
import android.content.Intent
import android.content.Intent.ACTION_QUICK_CLOCK
import android.graphics.Typeface.BOLD
import android.graphics.Typeface.NORMAL
import android.hardware.display.DisplayManager
import android.os.Handler
import android.os.Looper
import android.provider.AlarmClock.ACTION_SHOW_ALARMS
import android.provider.Settings
import android.provider.Settings.System.TIME_12_24
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.Display
import android.view.Display.STATE_OFF
import android.view.View
import android.widget.RemoteViews
import by.rudkouski.widget.R
import by.rudkouski.widget.database.DBHelper.Companion.INSTANCE
import by.rudkouski.widget.entity.Location
import by.rudkouski.widget.entity.Weather
import by.rudkouski.widget.listener.LocationChangeListener
import by.rudkouski.widget.receiver.WeatherUpdateBroadcastReceiver
import by.rudkouski.widget.receiver.WidgetUpdateBroadcastReceiver
import by.rudkouski.widget.scheduler.UpdateWeatherScheduler
import by.rudkouski.widget.view.forecast.ForecastActivity
import by.rudkouski.widget.view.location.LocationActivity
import by.rudkouski.widget.view.weather.WeatherUtils
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.HOUR

class WidgetProvider : AppWidgetProvider() {

    private val dbHelper = INSTANCE

    companion object {
        private val WIDGET_UPDATE = "${WidgetProvider::class.java.`package`} WIDGET_UPDATE"
        private const val TIME_FORMAT_12 = "h:mm"
        private const val TIME_FORMAT_24 = "H:mm"
        private const val DATE_WITH_DAY_SHORT_FORMAT = "EEE, dd MMM"
        private const val WIDGET_CLOCK_UPDATE_REQUEST_CODE = 1234
        private const val SYSTEM_TIME_FORMAT_24 = 24

        fun updateWidget(context: Context) {
            val intent = Intent(context, WidgetProvider::class.java)
            intent.action = WIDGET_UPDATE
            PendingIntent.getBroadcast(context, WIDGET_CLOCK_UPDATE_REQUEST_CODE, intent, FLAG_UPDATE_CURRENT).send()
        }

        fun chooseSystemTimeFormat(context: Context, timeFormat12: String, timeFormat24: String): String {
            return if (Settings.System.getInt(context.contentResolver, TIME_12_24, 0) == SYSTEM_TIME_FORMAT_24)
                timeFormat24 else timeFormat12
        }

        fun isActualWeather(weather: Weather?): Boolean {
            if (weather != null) {
                val actualCalendar = Calendar.getInstance()
                actualCalendar.time = weather.date
                actualCalendar.add(HOUR, 4)
                return Calendar.getInstance().before(actualCalendar)
            }
            return false
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (isDisplayOn(context) && WIDGET_UPDATE == intent.action) {
            val componentName = ComponentName(context, javaClass.name)
            val widgetManager = AppWidgetManager.getInstance(context)
            val widgetIds = widgetManager.getAppWidgetIds(componentName)
            for (widgetId in widgetIds) {
                val handler = Handler(Looper.getMainLooper())
                handler.post {
                    val remoteViews = updateWidget(context, widgetId)
                    widgetManager.updateAppWidget(widgetId, remoteViews)
                }
            }
        }
    }

    private fun isDisplayOn(context: Context): Boolean {
        val displayManager = context.getSystemService(DISPLAY_SERVICE) as DisplayManager
        return isAnyDisplayNotOff(displayManager.displays)
    }

    private fun isAnyDisplayNotOff(displays: Array<Display>): Boolean {
        for (display in displays) {
            if (display.state != STATE_OFF) {
                return true
            }
        }
        return false
    }

    private fun updateWidget(context: Context, widgetId: Int): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.widget)
        val widget = dbHelper.getWidgetById(widgetId)
        if (widget != null) {
            updateClockAndDate(remoteViews, context, widget.location.timeZone, widget.isBold)
            updateLocation(remoteViews, widget.location, widget.isBold)
            updateWeather(remoteViews, context, widget.location.id)
            setPendingIntents(remoteViews, context, widgetId)
        }
        return remoteViews
    }

    private fun updateClockAndDate(remoteViews: RemoteViews, context: Context, timeZone: TimeZone, isBold: Boolean) {
        val currentTime = Calendar.getInstance()
        val timeFormat =
            SimpleDateFormat(chooseSystemTimeFormat(context, TIME_FORMAT_12, TIME_FORMAT_24), Locale.getDefault())
        timeFormat.timeZone = timeZone
        remoteViews.setTextViewText(R.id.clock_widget, timeFormat.format(currentTime.time))
        val dateFormat = SimpleDateFormat(DATE_WITH_DAY_SHORT_FORMAT, Locale.getDefault())
        dateFormat.timeZone = timeZone
        val spanDateText = createSpannableString(dateFormat.format(currentTime.time), isBold)
        remoteViews.setTextViewText(R.id.date_widget, spanDateText)
    }

    private fun createSpannableString(resource: String, isBold: Boolean): SpannableString {
        val spanString = SpannableString(resource)
        spanString.setSpan(StyleSpan(if (isBold) BOLD else NORMAL), 0, spanString.length, 0)
        return spanString
    }

    private fun updateLocation(remoteViews: RemoteViews, location: Location, isBold: Boolean) {
        val spanLocationText = createSpannableString("  ${location.name}", isBold)
        remoteViews.setTextViewText(R.id.location_widget, spanLocationText)
    }

    private fun updateWeather(remoteViews: RemoteViews, context: Context, locationId: Int) {
        val weather = dbHelper.getWeatherByLocationId(locationId)
        if (isActualWeather(weather)) {
            remoteViews.setViewVisibility(R.id.weather_widget, View.VISIBLE)
            remoteViews.setImageViewResource(R.id.weather_image_widget,
                WeatherUtils.getWeatherImageResource(context, weather!!))
            remoteViews.setTextViewText(R.id.degrees_widget, Math.round(weather.temperature).toString())
            remoteViews.setTextViewText(R.id.degrees_text_widget, context.getString(R.string.temperature_unit))
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
        WidgetUpdateBroadcastReceiver.registerReceiver()
        LocationChangeListener.startLocationUpdate()
        updateWidget(context)
        WeatherUpdateBroadcastReceiver.updateWeather(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        UpdateWeatherScheduler.stopUpdateWeatherScheduler()
        WidgetUpdateBroadcastReceiver.unregisterReceiver()
        LocationChangeListener.stopLocationUpdate()
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)

        val handler = Handler(Looper.getMainLooper())
        handler.post {
            for (appWidgetId in appWidgetIds) {
                dbHelper.deleteWidgetById(appWidgetId)
            }
        }
    }
}