package by.rudkouski.widget.repository

import androidx.room.Transaction
import by.rudkouski.widget.database.AppDatabase
import by.rudkouski.widget.entity.Forecast
import kotlinx.coroutines.runBlocking

object ForecastRepository {

    private val forecastDao = AppDatabase.INSTANCE.forecastDao()

    fun getForecastById(forecastId: Int): Forecast? {
        return runBlocking {
            forecastDao.getById(forecastId)
        }
    }

    @Transaction
    fun setForecastsByLocationId(forecasts: List<Forecast>, locationId: Int) {
        runBlocking {
            val savedForecasts = forecastDao.getAllByLocationId(locationId)
            if (!savedForecasts.isNullOrEmpty()) {
                forecastDao.deleteAllForLocationId(locationId)
            }
            forecastDao.insertAll(forecasts)
        }
    }

    fun getForecastsByLocationId(locationId: Int): List<Forecast>? {
        return runBlocking {
            forecastDao.getAllByLocationId(locationId)
        }
    }

    fun deleteForecastsForLocationId(locationId: Int) {
        runBlocking {
            forecastDao.deleteAllForLocationId(locationId)
        }
    }
}