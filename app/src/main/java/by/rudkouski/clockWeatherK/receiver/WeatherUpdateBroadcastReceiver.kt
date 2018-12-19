package by.rudkouski.clockWeatherK.receiver

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import by.rudkouski.clockWeatherK.database.DBHelper.Companion.INSTANCE
import by.rudkouski.clockWeatherK.provider.WidgetProvider
import by.rudkouski.clockWeatherK.view.forecast.ForecastActivity
import by.rudkouski.clockWeatherK.view.weather.WeatherJsonConverter
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.*
import java.util.concurrent.Executors

class WeatherUpdateBroadcastReceiver : BroadcastReceiver() {

    private val executorService = Executors.newFixedThreadPool(1)
    private val dbHelper = INSTANCE
    private val networkChangeBroadcastReceiver = NetworkChangeChecker

    companion object {
        private const val WEATHER_UPDATE_REQUEST_CODE = 5678
        private const val WEATHER_UPDATE = "by.rudkouski.clockWeatherK.widget.WEATHER_UPDATE"
        /*There is used Yahoo! Weather as data provider(https://developer.yahoo.com/weather/)*/
        private const val WEATHER_QUERY_BY_COORDINATES =
            "https://query.yahooapis.com/v1/public/yql?q=select * from weather.forecast where woeid in (select woeid from geo.places(1) where " + "text=\"(%1\$s, %2\$s)\") and u='c' &format=json"

        fun getUpdateWeatherPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, WeatherUpdateBroadcastReceiver::class.java)
            intent.action = WEATHER_UPDATE
            return PendingIntent.getBroadcast(context, WEATHER_UPDATE_REQUEST_CODE, intent, FLAG_UPDATE_CURRENT)
        }

        fun updateWeather(context: Context) {
            getUpdateWeatherPendingIntent(context).send()
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (WEATHER_UPDATE == intent.action) {
            if (networkChangeBroadcastReceiver.isOnline()) {
                updateWeather(context)
            } else {
                networkChangeBroadcastReceiver.registerReceiver()
            }
        }
    }

    private fun updateWeather(context: Context) {
        executorService.execute {
            val locationIds = dbHelper.getLocationIdsContainedInAllWidgets()
            for (locationId in locationIds) {
                val location = dbHelper.getLocationById(locationId)
                try {
                    val responseBody = getResponseBodyForLocationCoordinates(location.latitude, location.longitude)
                    if (responseBody != null) {
                        val currentWeather = WeatherJsonConverter.getWeatherFromResponseBody(responseBody)
                        if (dbHelper.setWeatherByLocationId(currentWeather, locationId)) {
                            val forecasts = WeatherJsonConverter.getForecastsFromResponseBody(responseBody)
                            dbHelper.setForecastsByLocationId(forecasts, locationId)
                        }
                    }
                } catch (e: Throwable) {
                    Log.e(this.javaClass.simpleName, e.toString())
                    continue
                }
            }
            sendIntentsForWidgetUpdate(context)
        }
    }

    private fun getResponseBodyForLocationCoordinates(latitude: Double, longitude: Double): String? {
        val request = String.format(Locale.getDefault(), WEATHER_QUERY_BY_COORDINATES, latitude, longitude)
        return getResponseBodyForRequest(request)
    }

    private fun getResponseBodyForRequest(req: String): String? {
        val client = OkHttpClient.Builder().build()
        val request = Request.Builder().url(req).build()
        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    return responseBody.string()
                }
            }
        }
        return null
    }

    private fun sendIntentsForWidgetUpdate(context: Context) {
        WidgetProvider.updateWidget(context)
        ForecastActivity.updateActivityBroadcast(context)
    }
}