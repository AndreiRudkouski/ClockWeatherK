package by.rudkouski.clockWeatherK.receiver

import android.app.AlarmManager
import android.app.AlarmManager.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.content.Intent.ACTION_BOOT_COMPLETED
import android.content.Intent.ACTION_LOCKED_BOOT_COMPLETED
import android.text.format.DateUtils.MINUTE_IN_MILLIS
import android.text.format.DateUtils.SECOND_IN_MILLIS
import by.rudkouski.clockWeatherK.app.App
import by.rudkouski.clockWeatherK.provider.WidgetProvider
import java.util.*
import java.util.Calendar.*

class RebootBroadcastReceiver : BroadcastReceiver() {

    companion object {
        fun startScheduledWeatherUpdate() {
            val alarmManager = App.appContext.getSystemService(ALARM_SERVICE) as AlarmManager
            alarmManager.setRepeating(RTC, System.currentTimeMillis() + getUpdateStart(), INTERVAL_HALF_HOUR,
                WeatherUpdateBroadcastReceiver.getUpdateWeatherPendingIntent(App.appContext))
        }

        private fun getUpdateStart(): Long {
            val currentTime = Calendar.getInstance()
            val millisInCurrentHour = currentTime.get(MINUTE) * MINUTE_IN_MILLIS + (currentTime.get(SECOND)
                * SECOND_IN_MILLIS - currentTime.get(MILLISECOND))
            return when {
                millisInCurrentHour < INTERVAL_FIFTEEN_MINUTES -> INTERVAL_FIFTEEN_MINUTES - millisInCurrentHour
                millisInCurrentHour < INTERVAL_FIFTEEN_MINUTES * 3 -> INTERVAL_FIFTEEN_MINUTES * 3 - millisInCurrentHour
                else -> INTERVAL_HOUR - millisInCurrentHour + INTERVAL_FIFTEEN_MINUTES
            }
        }

        fun stopScheduledWeatherUpdate() {
            val alarmManager = App.appContext.getSystemService(ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(WeatherUpdateBroadcastReceiver.getUpdateWeatherPendingIntent(App.appContext))
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (ACTION_BOOT_COMPLETED == intent.action || ACTION_LOCKED_BOOT_COMPLETED == intent.action) {
            startScheduledWeatherUpdate()
            WidgetUpdateBroadcastReceiver.registerReceiver()
            LocationChangeChecker.startLocationUpdate()
            WidgetProvider.updateWidgetPendingIntent(context)
            WeatherUpdateBroadcastReceiver.updateWeatherPendingIntent(context)
        }
    }
}