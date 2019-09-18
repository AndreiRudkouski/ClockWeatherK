package by.rudkouski.widget.repository

import androidx.room.Transaction
import by.rudkouski.widget.database.AppDatabase.Companion.INSTANCE
import by.rudkouski.widget.entity.Widget
import by.rudkouski.widget.repository.ForecastRepository.deleteForecastsForLocationId
import by.rudkouski.widget.repository.LocationRepository.isLocationNotUsed
import by.rudkouski.widget.repository.SettingRepository.setDefaultSettingsByWidgetId
import by.rudkouski.widget.repository.WeatherRepository.deleteWeathersForLocationId
import kotlinx.coroutines.runBlocking

object WidgetRepository {

    private val widgetDao = INSTANCE.widgetDao()

    fun getWidgetById(widgetId: Int): Widget? {
        return runBlocking {
            widgetDao.getById(widgetId)
        }
    }

    @Transaction
    fun deleteWidgetById(widgetId: Int) {
        runBlocking {
            val savedWidget = widgetDao.getById(widgetId)
            if (savedWidget != null) {
                widgetDao.delete(savedWidget)
                deleteWeathersAndForecastsForUnusedLocationId(savedWidget.locationId)
            }
        }
    }

    @Transaction
    fun setWidgetByIdAndLocationId(widgetId: Int, locationId: Int): Boolean {
        return runBlocking {
            val savedWidget = widgetDao.getById(widgetId)
            if (savedWidget != null) {
                if (savedWidget.locationId == locationId) {
                    return@runBlocking false
                }
                val updatedWidget = savedWidget.copy(locationId = locationId)
                widgetDao.update(updatedWidget)
                if (savedWidget.locationId != locationId) {
                    deleteWeathersAndForecastsForUnusedLocationId(savedWidget.locationId)
                }
            } else {
                val newWidget = Widget(widgetId, locationId)
                widgetDao.insert(newWidget)
                setDefaultSettingsByWidgetId(widgetId)
            }
            return@runBlocking true
        }
    }

    private fun deleteWeathersAndForecastsForUnusedLocationId(locationId: Int) {
        if (isLocationNotUsed(locationId)) {
            deleteWeathersForLocationId(locationId)
            deleteForecastsForLocationId(locationId)
        }
    }
}