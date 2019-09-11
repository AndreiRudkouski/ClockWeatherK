package by.rudkouski.widget.update.scheduler

import android.app.AlarmManager
import android.app.AlarmManager.*
import android.content.Context.ALARM_SERVICE
import android.text.format.DateUtils.MINUTE_IN_MILLIS
import android.text.format.DateUtils.SECOND_IN_MILLIS
import by.rudkouski.widget.app.App.Companion.appContext
import by.rudkouski.widget.update.receiver.LocationUpdateBroadcastReceiver.Companion.getLocationUpdatePendingIntent
import by.rudkouski.widget.update.receiver.WeatherUpdateBroadcastReceiver.Companion.getWeatherUpdatePendingIntent
import java.lang.System.currentTimeMillis
import java.util.Calendar.*

object UpdateWeatherScheduler {

    private const val WEATHER_UPDATE_INTERVAL_IN_MILLIS = INTERVAL_HALF_HOUR
    const val WEATHER_UPDATE_INTERVAL_IN_MINUTES = INTERVAL_HALF_HOUR / (60 * 1000)
    private const val LOCATION_UPDATE_INTERVAL_IN_MILLIS = INTERVAL_FIFTEEN_MINUTES
    const val LOCATION_UPDATE_INTERVAL_IN_MINUTES = INTERVAL_FIFTEEN_MINUTES / (60 * 1000)

    private val alarmManager = appContext.getSystemService(ALARM_SERVICE) as AlarmManager

    fun startWeatherUpdateScheduler() {
        alarmManager.setRepeating(RTC, currentTimeMillis() + getWeatherUpdateStartInterval(), WEATHER_UPDATE_INTERVAL_IN_MILLIS,
            getWeatherUpdatePendingIntent(appContext))
    }

    fun stopWeatherUpdateScheduler() {
        alarmManager.cancel(getWeatherUpdatePendingIntent(appContext))
    }

    fun startLocationUpdateScheduler() {
        alarmManager.setRepeating(RTC, currentTimeMillis() + getLocationUpdateStartInterval(), LOCATION_UPDATE_INTERVAL_IN_MILLIS,
            getLocationUpdatePendingIntent(appContext))
    }

    fun stopLocationUpdateScheduler() {
        alarmManager.cancel(getLocationUpdatePendingIntent(appContext))
    }

    private fun getWeatherUpdateStartInterval(): Long {
        val millisInCurrentHour = getMillisInCurrentHour()
        return when {
            millisInCurrentHour < INTERVAL_FIFTEEN_MINUTES -> INTERVAL_FIFTEEN_MINUTES - millisInCurrentHour
            millisInCurrentHour < INTERVAL_FIFTEEN_MINUTES * 3 -> INTERVAL_FIFTEEN_MINUTES * 3 - millisInCurrentHour
            else -> INTERVAL_HOUR - millisInCurrentHour + INTERVAL_FIFTEEN_MINUTES
        }
    }

    private fun getLocationUpdateStartInterval(): Long {
        val millisInCurrentHour = getMillisInCurrentHour()
        return when {
            millisInCurrentHour < INTERVAL_FIFTEEN_MINUTES -> INTERVAL_FIFTEEN_MINUTES - millisInCurrentHour
            millisInCurrentHour < INTERVAL_HALF_HOUR -> INTERVAL_HALF_HOUR - millisInCurrentHour
            millisInCurrentHour < INTERVAL_HALF_HOUR + INTERVAL_FIFTEEN_MINUTES -> INTERVAL_HALF_HOUR + INTERVAL_FIFTEEN_MINUTES - millisInCurrentHour
            else -> INTERVAL_HOUR - millisInCurrentHour
        }
    }

    private fun getMillisInCurrentHour(): Long {
        val currentTime = getInstance()
        return currentTime.get(MINUTE) * MINUTE_IN_MILLIS + (currentTime.get(SECOND) * SECOND_IN_MILLIS - currentTime.get(MILLISECOND))
    }
}