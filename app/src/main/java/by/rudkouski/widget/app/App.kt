package by.rudkouski.widget.app

import android.app.Application
import android.content.Context
import com.rohitss.uceh.UCEHandler
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
        UCEHandler.Builder(applicationContext).build()
    }

    private fun getProperty(key: String, context: Context): String {
        val properties = Properties()
        val assetManager = context.assets
        val inputStream = assetManager.open("config.properties")
        properties.load(inputStream)
        return properties.getProperty(key)

    }
}