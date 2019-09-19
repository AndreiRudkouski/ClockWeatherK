package by.rudkouski.widget.update.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.content.IntentFilter
import by.rudkouski.widget.app.App.Companion.locationUpdateInMinutes
import by.rudkouski.widget.app.App.Companion.weatherUpdateInMinutes
import by.rudkouski.widget.entity.Location
import by.rudkouski.widget.entity.Location.Companion.CURRENT_LOCATION_ID
import by.rudkouski.widget.provider.WidgetProvider.Companion.updateWidget
import by.rudkouski.widget.repository.LocationRepository.getAllUsedLocations
import by.rudkouski.widget.repository.WeatherRepository.getCurrentWeatherByLocationId
import by.rudkouski.widget.update.receiver.LocationUpdateBroadcastReceiver.Companion.updateCurrentLocation
import by.rudkouski.widget.update.receiver.WeatherUpdateBroadcastReceiver.Companion.updateOtherWeathers
import org.threeten.bp.OffsetDateTime.now

object WidgetUpdateBroadcastReceiver : BroadcastReceiver() {

    private val intentFilter: IntentFilter =
        IntentFilter().also { it.addAction(ACTION_TIME_TICK) }.also { it.addAction(ACTION_TIMEZONE_CHANGED) }
            .also { it.addAction(ACTION_TIME_CHANGED) }.also { it.addAction(ACTION_DATE_CHANGED) }
            .also { it.addAction(ACTION_SCREEN_ON) }.also { it.addAction(ACTION_LOCALE_CHANGED) }
            .also { it.addAction(ACTION_MY_PACKAGE_REPLACED) }

    fun registerWidgetUpdateReceiver(context: Context) {
        context.registerReceiver(this, intentFilter)
    }

    fun unregisterWidgetUpdateReceiver(context: Context) {
        context.applicationContext.unregisterReceiver(this)
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (ACTION_LOCALE_CHANGED == intent.action) {
            updateOtherWeathers(context)
            updateCurrentLocation(context)
        }
        if (ACTION_SCREEN_ON == intent.action) {
            val locations = getAllUsedLocations()
            if (!locations.isNullOrEmpty()) {
                if (locations.any { CURRENT_LOCATION_ID == it.id && isWeatherNeedUpdate(it, locationUpdateInMinutes)}) {
                    updateCurrentLocation(context)
                }
                if (locations.any { CURRENT_LOCATION_ID != it.id && isWeatherNeedUpdate(it, weatherUpdateInMinutes)}) {
                    updateOtherWeathers(context)
                }
            }
            return
        }
        updateWidget(context)
    }

    private fun isWeatherNeedUpdate(location: Location, interval: Long): Boolean {
        val weather = getCurrentWeatherByLocationId(location.id)
         return weather == null || weather.update.plusMinutes(interval).isBefore(now(location.zoneId))
    }

}