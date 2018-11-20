package by.rudkouski.clockWeatherK.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import by.rudkouski.clockWeatherK.app.App
import by.rudkouski.clockWeatherK.receiver.WidgetUpdateBroadcastReceiver.Companion.INSTANCE

class WidgetUpdateService : Service() {

    private val widgetUpdateBroadcastReceiver = INSTANCE

    companion object {
        fun startService() {
            val widgetUpdateService = Intent(App.appContext, WidgetUpdateService::class.java)
            App.appContext.startService(widgetUpdateService)
        }

        fun stopService() {
            val widgetUpdateService = Intent(App.appContext, WidgetUpdateService::class.java)
            App.appContext.stopService(widgetUpdateService)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        widgetUpdateBroadcastReceiver.registerReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        widgetUpdateBroadcastReceiver.unregisterReceiver()
    }
}