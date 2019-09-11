package by.rudkouski.widget.repository

import androidx.room.Transaction
import by.rudkouski.widget.database.AppDatabase.Companion.INSTANCE
import by.rudkouski.widget.entity.Location.Companion.CURRENT_LOCATION_ID
import by.rudkouski.widget.entity.Weather
import by.rudkouski.widget.repository.LocationRepository.getLocationById
import by.rudkouski.widget.repository.LocationRepository.resetCurrentLocation
import by.rudkouski.widget.update.receiver.LocationUpdateBroadcastReceiver.Companion.isPermissionsDenied
import by.rudkouski.widget.update.scheduler.UpdateWeatherScheduler.WEATHER_UPDATE_INTERVAL_IN_MINUTES
import kotlinx.coroutines.runBlocking
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.OffsetDateTime.now

object WeatherRepository {

    private val weatherDao = INSTANCE.weatherDao()

    @Transaction
    fun setCurrentWeather(currentWeather: Weather, locationId: Int) {
        runBlocking {
            val savedWeathers = weatherDao.getAllByLocationIdAndType(locationId, Weather.Type.CURRENT.name)
            if (!savedWeathers.isNullOrEmpty()) {
                weatherDao.delete(savedWeathers[0])
            }
            weatherDao.insert(currentWeather)
        }
    }

    @Transaction
    fun getCurrentWeatherByLocationId(locationId: Int): Weather? {
        return runBlocking {
            if (CURRENT_LOCATION_ID == locationId && isPermissionsDenied()) {
                resetCurrentLocation()
                return@runBlocking null
            }
            val weathers = weatherDao.getAllByLocationIdAndType(locationId, Weather.Type.CURRENT.name)
            return@runBlocking if (weathers.isNullOrEmpty()) null else weathers[0]
        }
    }

    fun deleteWeathersForLocationId(locationId: Int) {
        runBlocking {
            weatherDao.deleteAllForLocationId(locationId)
        }
    }

    @Transaction
    fun setHourWeathersByLocationId(weathers: List<Weather>, locationId: Int) {
        runBlocking {
            val savedHourWeathers = weatherDao.getAllByLocationIdAndType(locationId, Weather.Type.HOUR.name)
            if (!savedHourWeathers.isNullOrEmpty()) {
                weatherDao.deleteAllForLocationIdAndType(locationId, Weather.Type.HOUR.name)
            }
            weatherDao.insertAll(weathers)
        }
    }

    fun getHourWeathersByLocationIdAndTimeInterval(locationId: Int, timeFrom: OffsetDateTime, timeTo: OffsetDateTime): List<Weather>? {
        return runBlocking {
            weatherDao.getAllByParamsAndTimeInterval(locationId, Weather.Type.HOUR.name, timeFrom.toString(), timeTo.toString())
        }
    }

    fun getWeatherById(weatherId: Int): Weather? {
        return runBlocking {
            weatherDao.getById(weatherId)
        }
    }

    @Transaction
    fun setSuitableWeatherAsCurrentByLocationId(locationId: Int) {
        runBlocking {
            val zoneId = getLocationById(locationId).zoneId
            val locationTime = now(zoneId)
            val weathers = weatherDao.getAllByLocationIdAndType(locationId, Weather.Type.CURRENT.name)
            if (!weathers.isNullOrEmpty()) {
                if (weathers[0].date.plusMinutes(WEATHER_UPDATE_INTERVAL_IN_MINUTES * 2).isBefore(locationTime)) {
                    val timeFrom = locationTime.plusHours(-1).toString()
                    val timeTo = locationTime.toString()
                    val suitableWeathers = weatherDao.getAllByParamsAndTimeInterval(locationId, Weather.Type.HOUR.name, timeFrom, timeTo)
                    if (!suitableWeathers.isNullOrEmpty() && weathers[0].id != suitableWeathers[0].id) {
                        weatherDao.delete(weathers[0])
                        suitableWeathers[0].type = Weather.Type.CURRENT
                        weatherDao.update(suitableWeathers[0])
                    } else if (weathers[0].date.plusHours(4).isBefore(locationTime)) {
                        weatherDao.delete(weathers[0])
                    }
                }
            }
        }
    }
}