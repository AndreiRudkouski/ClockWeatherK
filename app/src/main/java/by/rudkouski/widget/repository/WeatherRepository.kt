package by.rudkouski.widget.repository

import androidx.room.Transaction
import by.rudkouski.widget.database.AppDatabase.Companion.INSTANCE
import by.rudkouski.widget.entity.Location.Companion.CURRENT_LOCATION_ID
import by.rudkouski.widget.entity.Weather
import by.rudkouski.widget.repository.LocationRepository.getLocationById
import by.rudkouski.widget.repository.LocationRepository.resetCurrentLocation
import by.rudkouski.widget.update.listener.LocationChangeListener.isPermissionsDenied
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object WeatherRepository {

    private val weatherDao = INSTANCE.weatherDao()

    @Transaction
    fun setCurrentWeather(currentWeather: Weather, locationId: Int) {
        GlobalScope.launch {
            val savedWeathers = weatherDao.getAllByLocationIdAndType(locationId, Weather.WeatherType.CURRENT.name)
            currentWeather.also { it.locationId = locationId }.also { it.type = Weather.WeatherType.CURRENT }
            if (savedWeathers.isNullOrEmpty()) {
                weatherDao.insert(currentWeather)
            } else {
                val updatedWeather = currentWeather.copy(id = savedWeathers[0].id)
                weatherDao.update(updatedWeather)
            }
        }
    }

    @Transaction
    fun getCurrentWeatherByLocationId(locationId: Int): Weather? {
        return runBlocking {
            if (CURRENT_LOCATION_ID == locationId && isPermissionsDenied()) {
                resetCurrentLocation()
                return@runBlocking null
            }
            val weathers = weatherDao.getAllByLocationIdAndType(locationId, Weather.WeatherType.CURRENT.name)
            val location = getLocationById(locationId)
            return@runBlocking if (weathers.isNullOrEmpty()) null else weathers[0].also { it.date.timeZone = location.timeZone }
        }
    }

    @Transaction
    fun deleteWeathersForLocationId(locationId: Int) {
        GlobalScope.launch {
            weatherDao.deleteAllForLocationIdAndType(locationId, Weather.WeatherType.CURRENT.name)
            weatherDao.deleteAllForLocationIdAndType(locationId, Weather.WeatherType.HOUR.name)
        }
    }

    @Transaction
    fun setHourWeathersByLocationId(weathers: List<Weather>, locationId: Int) {
        GlobalScope.launch {
            val savedHourWeathers = weatherDao.getAllByLocationIdAndType(locationId, Weather.WeatherType.HOUR.name)
            weathers.forEach {
                it.locationId = locationId
                it.type = Weather.WeatherType.HOUR
            }
            if (!savedHourWeathers.isNullOrEmpty()) {
                weatherDao.deleteAllForLocationIdAndType(locationId, Weather.WeatherType.HOUR.name)
            }
            weatherDao.insertAll(weathers)
        }
    }

    fun getHourWeathersByLocationId(locationId: Int): List<Weather>? {
        return runBlocking {
            val locations = weatherDao.getAllByLocationIdAndType(locationId, Weather.WeatherType.HOUR.name)
            val location = getLocationById(locationId)
            locations?.forEach { it.date.timeZone = location.timeZone }
            return@runBlocking locations
        }
    }

    fun getWeatherById(weatherId: Int): Weather? {
        return runBlocking {
            weatherDao.getById(weatherId)
        }
    }
}