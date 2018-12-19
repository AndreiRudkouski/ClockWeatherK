package by.rudkouski.clockWeatherK.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.content.IntentFilter
import by.rudkouski.clockWeatherK.app.App
import by.rudkouski.clockWeatherK.provider.WidgetProvider
import java.util.concurrent.atomic.AtomicBoolean

object WidgetUpdateBroadcastReceiver : BroadcastReceiver() {

    private val intentFilter: IntentFilter =
        IntentFilter().also { it.addAction(ACTION_TIME_TICK) }.also { it.addAction(ACTION_TIMEZONE_CHANGED) }
            .also { it.addAction(ACTION_TIME_CHANGED) }.also { it.addAction(ACTION_DATE_CHANGED) }
            .also { it.addAction(ACTION_SCREEN_ON) }.also { it.addAction(ACTION_LOCALE_CHANGED) }
    private val isRegistered = AtomicBoolean(false)

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

    override fun onReceive(context: Context, intent: Intent) = WidgetProvider.updateWidget(context)
}