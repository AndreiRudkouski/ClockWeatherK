package by.rudkouski.widget.app

import android.app.Application
import android.content.Context
import by.rudkouski.widget.update.receiver.LocationUpdateBroadcastReceiver
import by.rudkouski.widget.update.receiver.WidgetUpdateBroadcastReceiver
import by.rudkouski.widget.update.scheduler.UpdateWeatherScheduler
import java.util.*


class App : Application() {

    companion object {
        lateinit var appContext: App
        lateinit var apiKey: String
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
        apiKey = getProperty("apiKey", this)
        WidgetUpdateBroadcastReceiver.registerReceiver(this)
        UpdateWeatherScheduler.startWeatherUpdateScheduler()
        UpdateWeatherScheduler.startLocationUpdateScheduler()
        LocationUpdateBroadcastReceiver.setCurrentLocation()
    }

    override fun onTerminate() {
        super.onTerminate()
        WidgetUpdateBroadcastReceiver.unregisterReceiver(this)
        UpdateWeatherScheduler.stopWeatherUpdateScheduler()
        UpdateWeatherScheduler.stopLocationUpdateScheduler()
    }

    private fun getProperty(key: String, context: Context): String {
        val properties = Properties()
        val assetManager = context.assets
        val inputStream = assetManager.open("config.properties")
        properties.load(inputStream)
        return properties.getProperty(key)
    }
}