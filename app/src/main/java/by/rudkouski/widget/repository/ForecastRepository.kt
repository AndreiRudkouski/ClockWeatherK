package by.rudkouski.widget.repository

import androidx.room.Transaction
import by.rudkouski.widget.database.AppDatabase
import by.rudkouski.widget.entity.Forecast
import by.rudkouski.widget.repository.LocationRepository.getLocationById
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

object ForecastRepository {

    private val forecastDao = AppDatabase.INSTANCE.forecastDao()

    fun getForecastById(forecastId: Int): Forecast? {
        return runBlocking {
            val forecast = forecastDao.getById(forecastId)
            if (forecast?.locationId != null) {
                val location = getLocationById(forecast.locationId!!)
                setTimeZone(forecast, location.timeZone)
            }
            return@runBlocking forecast
        }
    }

    @Transaction
    fun setForecastsByLocationId(forecasts: List<Forecast>, locationId: Int) {
        GlobalScope.launch {
            val savedForecasts = forecastDao.getAllByLocationId(locationId)
            forecasts.forEach { it.locationId = locationId }
            if (!savedForecasts.isNullOrEmpty()) {
                forecastDao.deleteAllForLocationId(locationId)
            }
            forecastDao.insertAll(forecasts)
        }
    }

    fun getForecastsByLocationId(locationId: Int): List<Forecast>? {
        return runBlocking {
            val location = getLocationById(locationId)
            val forecasts = forecastDao.getAllByLocationId(locationId)
            forecasts?.forEach { setTimeZone(it, location.timeZone) }
            return@runBlocking forecasts
        }
    }

    fun deleteForecastsForLocationId(locationId: Int) {
        GlobalScope.launch {
            forecastDao.deleteAllForLocationId(locationId)
        }
    }

    private fun setTimeZone(forecast: Forecast, timeZone: TimeZone) {
        forecast.date.timeZone = timeZone
        forecast.sunriseTime.timeZone = timeZone
        forecast.sunsetTime.timeZone = timeZone
        forecast.precipitationIntensityMaxTime?.timeZone = timeZone
        forecast.apparentTemperatureLowTime.timeZone = timeZone
        forecast.apparentTemperatureHighTime.timeZone = timeZone
        forecast.temperatureLowTime.timeZone = timeZone
        forecast.temperatureHighTime.timeZone = timeZone
    }
}