package by.rudkouski.widget.app

import android.app.Application
import android.content.Context
import androidx.room.Room.databaseBuilder
import by.rudkouski.widget.database.AppDatabase
import by.rudkouski.widget.update.listener.LocationChangeListener
import by.rudkouski.widget.update.receiver.WeatherUpdateBroadcastReceiver
import by.rudkouski.widget.update.receiver.WidgetUpdateBroadcastReceiver
import by.rudkouski.widget.update.scheduler.UpdateWeatherScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*


class App : Application() {

    companion object {
        lateinit var appContext: App
        lateinit var db: AppDatabase
        lateinit var apiKey: String
        val uiScope = CoroutineScope(Dispatchers.Main + Job())
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
        db = databaseBuilder(this, AppDatabase::class.java, "clock_weather_database").build()
        apiKey = getProperty("apiKey", this)
        uiScope.launch { db.locationDao().addDefaultLocations() }
        WidgetUpdateBroadcastReceiver.registerReceiver()
        UpdateWeatherScheduler.startUpdateWeatherScheduler()
        LocationChangeListener.startLocationUpdate()
        WeatherUpdateBroadcastReceiver.updateAllWeathers(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        WidgetUpdateBroadcastReceiver.unregisterReceiver()
        UpdateWeatherScheduler.stopUpdateWeatherScheduler()
        LocationChangeListener.stopLocationUpdate()
    }

    private fun getProperty(key: String, context: Context): String {
        val properties = Properties()
        val assetManager = context.assets
        val inputStream = assetManager.open("config.properties")
        properties.load(inputStream)
        return properties.getProperty(key)
    }
}