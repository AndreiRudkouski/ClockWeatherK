package by.rudkouski.clockWeatherK.app

import android.app.Application

class App : Application() {

    companion object {
        lateinit var appContext: App
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }
}