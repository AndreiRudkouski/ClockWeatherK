package by.rudkouski.widget.app

import android.app.Application
import android.content.Context
import by.rudkouski.widget.update.receiver.LocationUpdateBroadcastReceiver.Companion.setCurrentLocation
import by.rudkouski.widget.update.receiver.WidgetUpdateBroadcastReceiver.registerWidgetUpdateReceiver
import by.rudkouski.widget.update.receiver.WidgetUpdateBroadcastReceiver.unregisterWidgetUpdateReceiver
import by.rudkouski.widget.update.scheduler.UpdateWeatherScheduler.startLocationUpdateScheduler
import by.rudkouski.widget.update.scheduler.UpdateWeatherScheduler.startWeatherUpdateScheduler
import by.rudkouski.widget.update.scheduler.UpdateWeatherScheduler.stopLocationUpdateScheduler
import by.rudkouski.widget.update.scheduler.UpdateWeatherScheduler.stopWeatherUpdateScheduler
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
        registerWidgetUpdateReceiver(this)
        startWeatherUpdateScheduler()
        startLocationUpdateScheduler()
        setCurrentLocation()
    }

    override fun onTerminate() {
        super.onTerminate()
        unregisterWidgetUpdateReceiver(this)
        stopWeatherUpdateScheduler()
        stopLocationUpdateScheduler()
    }

    private fun getProperty(key: String, context: Context): String {
        val properties = Properties()
        val assetManager = context.assets
        val inputStream = assetManager.open("config.properties")
        properties.load(inputStream)
        return properties.getProperty(key)
    }
}