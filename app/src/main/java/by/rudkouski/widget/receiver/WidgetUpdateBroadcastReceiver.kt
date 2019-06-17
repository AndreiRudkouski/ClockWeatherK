package by.rudkouski.widget.receiver

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.content.IntentFilter
import by.rudkouski.widget.app.App.Companion.appContext
import by.rudkouski.widget.database.DBHelper.Companion.INSTANCE
import by.rudkouski.widget.entity.Weather
import by.rudkouski.widget.listener.LocationChangeListener
import by.rudkouski.widget.provider.WidgetProvider
import java.util.*

object WidgetUpdateBroadcastReceiver : BroadcastReceiver() {

    private val intentFilter: IntentFilter =
        IntentFilter().also { it.addAction(ACTION_TIME_TICK) }.also { it.addAction(ACTION_TIMEZONE_CHANGED) }
            .also { it.addAction(ACTION_TIME_CHANGED) }.also { it.addAction(ACTION_DATE_CHANGED) }
            .also { it.addAction(ACTION_SCREEN_ON) }.also { it.addAction(ACTION_LOCALE_CHANGED) }
            .also { it.addAction(ACTION_MY_PACKAGE_REPLACED) }
    private val dbHelper = INSTANCE

    fun registerReceiver() {
        appContext.registerReceiver(this, intentFilter)
    }

    fun unregisterReceiver() {
        appContext.applicationContext.unregisterReceiver(this)
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (ACTION_LOCALE_CHANGED == intent.action) {
            WeatherUpdateBroadcastReceiver.updateWeather(context)
            LocationChangeListener.updateLocation()
        }
        if (ACTION_SCREEN_ON == intent.action) {
            val locationIds = dbHelper.getLocationIdsContainedInAllWidgets()
            for (locationId in locationIds) {
                val location = dbHelper.getLocationById(locationId)
                val weather = dbHelper.getWeatherByLocationId(locationId)
                if (isWeatherNeedUpdate(weather, location.timeZone)) {
                    WeatherUpdateBroadcastReceiver.updateWeather(context)
                    return
                }
            }
        }
        WidgetProvider.updateWidget(context)
    }

    private fun isWeatherNeedUpdate(weather: Weather?, timeZone: TimeZone) =
        weather == null
            || Calendar.getInstance(timeZone).time.time.minus(weather.date.time.time) >= AlarmManager.INTERVAL_HALF_HOUR
}