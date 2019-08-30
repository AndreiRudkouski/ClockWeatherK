package by.rudkouski.widget.update.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.content.IntentFilter
import by.rudkouski.widget.entity.Weather
import by.rudkouski.widget.provider.WidgetProvider.Companion.updateWidget
import by.rudkouski.widget.repository.LocationRepository.getAllUsedLocations
import by.rudkouski.widget.repository.WeatherRepository.getCurrentWeatherByLocationId
import by.rudkouski.widget.update.listener.LocationChangeListener
import by.rudkouski.widget.update.scheduler.UpdateWeatherScheduler.WEATHER_UPDATE_INTERVAL_IN_MINUTES
import org.threeten.bp.OffsetDateTime.now
import org.threeten.bp.ZoneId

object WidgetUpdateBroadcastReceiver : BroadcastReceiver() {

    private val intentFilter: IntentFilter =
        IntentFilter().also { it.addAction(ACTION_TIME_TICK) }.also { it.addAction(ACTION_TIMEZONE_CHANGED) }
            .also { it.addAction(ACTION_TIME_CHANGED) }.also { it.addAction(ACTION_DATE_CHANGED) }
            .also { it.addAction(ACTION_SCREEN_ON) }.also { it.addAction(ACTION_LOCALE_CHANGED) }
            .also { it.addAction(ACTION_MY_PACKAGE_REPLACED) }

    fun registerReceiver(context: Context) {
        context.registerReceiver(this, intentFilter)
    }

    fun unregisterReceiver(context: Context) {
        context.applicationContext.unregisterReceiver(this)
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
                    if (isWeatherNeedUpdate(weather, location.zoneId)) {
                        WeatherUpdateBroadcastReceiver.updateAllWeathers(context)
                        return
                    }
                }
            }
        }
        updateWidget(context)
    }

    private fun isWeatherNeedUpdate(weather: Weather?, zoneId: ZoneId) =
        weather == null || weather.update.plusMinutes(WEATHER_UPDATE_INTERVAL_IN_MINUTES).isBefore(now(zoneId))
}