package by.rudkouski.widget.update.scheduler

import android.app.AlarmManager
import android.app.AlarmManager.INTERVAL_HOUR
import android.app.AlarmManager.RTC
import android.content.Context.ALARM_SERVICE
import android.text.format.DateUtils.MINUTE_IN_MILLIS
import by.rudkouski.widget.app.App.Companion.appContext
import by.rudkouski.widget.app.App.Companion.locationUpdateInMinutes
import by.rudkouski.widget.app.App.Companion.weatherUpdateInMinutes
import by.rudkouski.widget.update.receiver.LocationUpdateBroadcastReceiver.Companion.getLocationUpdatePendingIntent
import by.rudkouski.widget.update.receiver.WeatherUpdateBroadcastReceiver.Companion.getWeatherUpdatePendingIntent
import java.lang.System.currentTimeMillis
import java.util.concurrent.TimeUnit

object UpdateWeatherScheduler {

    private val alarmManager = appContext.getSystemService(ALARM_SERVICE) as AlarmManager

    fun startWeatherUpdateScheduler() {
        val currentTimeMillis = currentTimeMillis()
        val weatherUpdateInMillis = weatherUpdateInMinutes * MINUTE_IN_MILLIS
        val triggerAtMillis = currentTimeMillis() + getIntervalInMillis(currentTimeMillis, weatherUpdateInMillis)
        alarmManager.setRepeating(RTC, triggerAtMillis, weatherUpdateInMillis, getWeatherUpdatePendingIntent(appContext))
    }

    fun stopWeatherUpdateScheduler() {
        alarmManager.cancel(getWeatherUpdatePendingIntent(appContext))
    }

    fun startLocationUpdateScheduler() {
        val currentTimeMillis = currentTimeMillis()
        val locationUpdateInMillis = locationUpdateInMinutes * MINUTE_IN_MILLIS
        val triggerAtMillis = currentTimeMillis() + getIntervalInMillis(currentTimeMillis, locationUpdateInMillis)
        alarmManager.setRepeating(RTC, triggerAtMillis, locationUpdateInMillis, getLocationUpdatePendingIntent(appContext))
    }

    fun stopLocationUpdateScheduler() {
        alarmManager.cancel(getLocationUpdatePendingIntent(appContext))
    }

    private fun getIntervalInMillis(currentTimeMillis: Long, updateIntervalMillis: Long): Long {
        val intervalInMillis = if (updateIntervalMillis > INTERVAL_HOUR) updateIntervalMillis - INTERVAL_HOUR else updateIntervalMillis
        val millisInCurrentHour = currentTimeMillis - TimeUnit.HOURS.toMillis(TimeUnit.MILLISECONDS.toHours(currentTimeMillis))
        return if (millisInCurrentHour > intervalInMillis) {
            intervalInMillis - millisInCurrentHour % intervalInMillis
        } else {
            intervalInMillis - millisInCurrentHour
        }
    }
}