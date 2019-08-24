package by.rudkouski.widget.update.receiver

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import by.rudkouski.widget.app.App
import by.rudkouski.widget.entity.Location
import by.rudkouski.widget.entity.Location.Companion.CURRENT_LOCATION_ID
import by.rudkouski.widget.provider.WidgetProvider
import by.rudkouski.widget.repository.ForecastRepository.setForecastsByLocationId
import by.rudkouski.widget.repository.LocationRepository.getAllUsedLocations
import by.rudkouski.widget.repository.LocationRepository.getLocationById
import by.rudkouski.widget.repository.LocationRepository.resetCurrentLocation
import by.rudkouski.widget.repository.LocationRepository.updateCurrentLocationTimeZoneName
import by.rudkouski.widget.repository.WeatherRepository.setCurrentWeather
import by.rudkouski.widget.repository.WeatherRepository.setHourWeathersByLocationId
import by.rudkouski.widget.update.listener.LocationChangeListener.isPermissionsDenied
import by.rudkouski.widget.view.forecast.ForecastActivity
import by.rudkouski.widget.view.weather.WeatherUtils
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.*
import java.util.concurrent.Executors

class WeatherUpdateBroadcastReceiver : BroadcastReceiver() {

    private val executorService = Executors.newFixedThreadPool(1)

    companion object {
        private const val WEATHER_UPDATE_REQUEST_CODE = 1002
        private const val CURRENT_WEATHER_UPDATE_REQUEST_CODE = 1003
        /*There is used Dark Sky API as data provider(https://darksky.net)*/
        private const val WEATHER_QUERY_BY_COORDINATES = "https://api.darksky.net/forecast/%1\$s/%2\$s,%3\$s?lang=%4\$s&units=si"

        private val weatherUpdateAction = "${WeatherUpdateBroadcastReceiver::class.java.`package`}.WEATHER_UPDATE"
        private val currentWeatherUpdateAction = "${WeatherUpdateBroadcastReceiver::class.java.`package`}.CURRENT_WEATHER_UPDATE"

        fun getUpdateWeatherPendingIntent(context: Context): PendingIntent {
            return getPendingIntent(context, weatherUpdateAction, WEATHER_UPDATE_REQUEST_CODE)
        }

        fun updateAllWeathers(context: Context) {
            getUpdateWeatherPendingIntent(context).send()
        }

        fun updateCurrentWeather(context: Context) {
            getPendingIntent(context, currentWeatherUpdateAction,
                CURRENT_WEATHER_UPDATE_REQUEST_CODE).send()
        }

        private fun getPendingIntent(context: Context, action: String, actionCode: Int): PendingIntent {
            val intent = Intent(context, WeatherUpdateBroadcastReceiver::class.java)
            intent.action = action
            return PendingIntent.getBroadcast(context, actionCode, intent, FLAG_UPDATE_CURRENT)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (weatherUpdateAction == intent.action || currentWeatherUpdateAction == intent.action) {
            if (NetworkChangeChecker.isOnline()) {
                if (weatherUpdateAction == intent.action) {
                    updateAllWeathers(context)
                } else {
                    updateCurrentWeather(context)
                }
            } else {
                NetworkChangeChecker.registerReceiver()
            }
        }
    }

    private fun updateAllWeathers(context: Context) {
        executorService.execute {
            val locations = getAllUsedLocations()
            if (locations != null) {
                for (location in locations) {
                    updateWeather(location)
                }
                sendIntentsForWidgetUpdate(context)
            }
        }
    }

    private fun updateCurrentWeather(context: Context) {
        executorService.execute {
            val location = getLocationById(CURRENT_LOCATION_ID)
            updateWeather(location)
            sendIntentsForWidgetUpdate(context)
        }
    }

    private fun updateWeather(location: Location) {
        if (CURRENT_LOCATION_ID == location.id && isPermissionsDenied()) {
            resetCurrentLocation()
            return
        }
        try {
            val responseBody = getResponseBodyForLocationCoordinates(location.latitude, location.longitude)
            if (responseBody != null) {
                if (location.id == CURRENT_LOCATION_ID) {
                    val currentTimeZone = WeatherUtils.getCurrentTimeZoneNameFromResponseBody(responseBody)
                    updateCurrentLocationTimeZoneName(currentTimeZone)
                }
                val currentWeather = WeatherUtils.getCurrentWeatherFromResponseBody(responseBody)
                setCurrentWeather(currentWeather, location.id)
                val hourWeathers = WeatherUtils.getHourWeathersFromResponseBody(responseBody)
                setHourWeathersByLocationId(hourWeathers, location.id)
                val forecasts = WeatherUtils.getDayForecastFromResponseBody(responseBody)
                setForecastsByLocationId(forecasts, location.id)
            }
        } catch (e: Throwable) {
            Log.e(this.javaClass.simpleName, e.message)
        }
    }

    private fun getResponseBodyForLocationCoordinates(latitude: Double, longitude: Double): String? {
        val request = String.format(Locale.getDefault(), WEATHER_QUERY_BY_COORDINATES, App.apiKey, latitude, longitude, Locale.getDefault().language)
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