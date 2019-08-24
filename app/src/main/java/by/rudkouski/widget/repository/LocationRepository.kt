package by.rudkouski.widget.repository

import androidx.room.Transaction
import by.rudkouski.widget.database.AppDatabase.Companion.INSTANCE
import by.rudkouski.widget.database.AppDatabase.Companion.defaultLocation
import by.rudkouski.widget.entity.Location
import by.rudkouski.widget.entity.Location.Companion.CURRENT_LOCATION_ID
import by.rudkouski.widget.repository.WeatherRepository.deleteWeatherForLocationId
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

object LocationRepository {

    private val locationDao = INSTANCE.locationDao()

    fun getAllLocations(): List<Location> {
        return runBlocking {
            locationDao.getAll()
        }
    }

    fun getAllUsedLocations(): List<Location>? {
        return runBlocking {
            locationDao.getAllUsed()
        }
    }

    fun getLocationById(locationId: Int): Location {
        return runBlocking {
            locationDao.getById(locationId)
        }
    }

    fun getLocationByWidgetId(widgetId: Int): Location? {
        return runBlocking {
            locationDao.getByWidgetId(widgetId)
        }
    }

    @Transaction
    fun updateCurrentLocationData(name: String,
                                  latitude: Double,
                                  longitude: Double) {
        GlobalScope.launch {
            val savedLocation = locationDao.getById(CURRENT_LOCATION_ID)
            val updatedLocation = savedLocation.copy(name_code = name, longitude = longitude, latitude = latitude)
            locationDao.update(updatedLocation)
        }
    }

    @Transaction
    fun updateCurrentLocationTimeZoneName(timeZone: TimeZone) {
        GlobalScope.launch {
            val savedLocation = locationDao.getById(CURRENT_LOCATION_ID)
            val updatedLocation = savedLocation.copy(timeZone = timeZone)
            locationDao.update(updatedLocation)
        }
    }

    @Transaction
    fun resetCurrentLocation() {
        GlobalScope.launch {
            deleteWeatherForLocationId(CURRENT_LOCATION_ID)
            locationDao.update(defaultLocation)
        }
    }

    fun isLocationUsed(locationId: Int): Boolean {
        return runBlocking {
            locationDao.getUsed(locationId).isNullOrEmpty()
        }
    }
}