package by.rudkouski.widget.repository

import androidx.room.Transaction
import by.rudkouski.widget.database.AppDatabase.Companion.INSTANCE
import by.rudkouski.widget.entity.Location
import by.rudkouski.widget.entity.Location.Companion.CURRENT_LOCATION_ID
import by.rudkouski.widget.entity.Location.Companion.DEFAULT_LOCATION
import by.rudkouski.widget.repository.ForecastRepository.deleteForecastsForLocationId
import by.rudkouski.widget.repository.WeatherRepository.deleteWeathersForLocationId
import kotlinx.coroutines.runBlocking
import org.threeten.bp.ZoneId

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
    fun updateCurrentLocationData(name: String, latitude: Double, longitude: Double) {
        runBlocking {
            val savedLocation = locationDao.getById(CURRENT_LOCATION_ID)
            val updatedLocation = savedLocation.copy(name_code = name, longitude = longitude, latitude = latitude)
            locationDao.update(updatedLocation)
        }
    }

    @Transaction
    fun updateCurrentLocationZoneIdName(zoneId: ZoneId) {
        runBlocking {
            val savedLocation = locationDao.getById(CURRENT_LOCATION_ID)
            val updatedLocation = savedLocation.copy(zoneId = zoneId)
            locationDao.update(updatedLocation)
        }
    }

    @Transaction
    fun resetCurrentLocation() {
        runBlocking {
            deleteWeathersForLocationId(CURRENT_LOCATION_ID)
            deleteForecastsForLocationId(CURRENT_LOCATION_ID)
            locationDao.update(DEFAULT_LOCATION)
        }
    }

    fun isLocationNotUsed(locationId: Int): Boolean {
        return runBlocking {
            locationDao.getUsed(locationId) == null
        }
    }
}