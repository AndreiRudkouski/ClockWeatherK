package by.rudkouski.clockWeatherK.receiver

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.content.IntentFilter
import by.rudkouski.clockWeatherK.app.App
import by.rudkouski.clockWeatherK.database.DBHelper.Companion.INSTANCE
import by.rudkouski.clockWeatherK.entity.Weather
import by.rudkouski.clockWeatherK.provider.WidgetProvider
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

object WidgetUpdateBroadcastReceiver : BroadcastReceiver() {

    private val intentFilter: IntentFilter =
        IntentFilter().also { it.addAction(ACTION_TIME_TICK) }.also { it.addAction(ACTION_TIMEZONE_CHANGED) }
            .also { it.addAction(ACTION_TIME_CHANGED) }.also { it.addAction(ACTION_DATE_CHANGED) }
            .also { it.addAction(ACTION_SCREEN_ON) }.also { it.addAction(ACTION_LOCALE_CHANGED) }
    private val isRegistered = AtomicBoolean(false)
    private val dbHelper = INSTANCE

    fun registerReceiver() {
        if (!isRegistered.get()) {
            App.appContext.registerReceiver(this, intentFilter)
            isRegistered.set(true)
        }
    }

    fun unregisterReceiver() {
        if (isRegistered.get()) {
            App.appContext.applicationContext.unregisterReceiver(this)
            isRegistered.set(false)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        WidgetProvider.updateWidget(context)
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
    }

    private fun isWeatherNeedUpdate(weather: Weather?, timeZone: TimeZone) =
        weather?.updateDate == null
            || Calendar.getInstance(timeZone).time.time.minus(weather.updateDate.time) >= AlarmManager.INTERVAL_HALF_HOUR
}