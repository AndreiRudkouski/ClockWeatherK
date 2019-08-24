package by.rudkouski.widget.update.receiver

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.content.IntentFilter
import by.rudkouski.widget.app.App.Companion.appContext
import by.rudkouski.widget.entity.Weather
import by.rudkouski.widget.provider.WidgetProvider
import by.rudkouski.widget.repository.LocationRepository.getAllUsedLocations
import by.rudkouski.widget.repository.WeatherRepository.getCurrentWeatherByLocationId
import by.rudkouski.widget.update.listener.LocationChangeListener
import java.util.*

object WidgetUpdateBroadcastReceiver : BroadcastReceiver() {

    private val intentFilter: IntentFilter =
        IntentFilter().also { it.addAction(ACTION_TIME_TICK) }.also { it.addAction(ACTION_TIMEZONE_CHANGED) }
            .also { it.addAction(ACTION_TIME_CHANGED) }.also { it.addAction(ACTION_DATE_CHANGED) }
            .also { it.addAction(ACTION_SCREEN_ON) }.also { it.addAction(ACTION_LOCALE_CHANGED) }
            .also { it.addAction(ACTION_MY_PACKAGE_REPLACED) }

    fun registerReceiver() {
        appContext.registerReceiver(this, intentFilter)
    }

    fun unregisterReceiver() {
        appContext.applicationContext.unregisterReceiver(this)
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (ACTION_LOCALE_CHANGED == intent.action) {
            WeatherUpdateBroadcastReceiver.updateAllWeathers(context)
            LocationChangeListener.updateLocation()
        }
        if (ACTION_SCREEN_ON == intent.action) {
            val locations = getAllUsedLocations()
            if (locations != null) {
                for (location in locations) {
                    val weather = getCurrentWeatherByLocationId(location.id)
                    if (isWeatherNeedUpdate(weather, location.timeZone)) {
                        WeatherUpdateBroadcastReceiver.updateAllWeathers(context)
                        return
                    }
                }
            }
        }
        WidgetProvider.updateWidget(context)
    }

    private fun isWeatherNeedUpdate(weather: Weather?, timeZone: TimeZone) =
        weather == null || Calendar.getInstance(timeZone).time.time.minus(weather.date.time.time) >= AlarmManager.INTERVAL_HALF_HOUR
}