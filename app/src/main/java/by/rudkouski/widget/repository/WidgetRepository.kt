package by.rudkouski.widget.repository

import androidx.room.Transaction
import by.rudkouski.widget.app.App.Companion.appContext
import by.rudkouski.widget.database.AppDatabase.Companion.INSTANCE
import by.rudkouski.widget.entity.Widget
import by.rudkouski.widget.repository.ForecastRepository.deleteForecastsForLocationId
import by.rudkouski.widget.repository.LocationRepository.isLocationUsed
import by.rudkouski.widget.repository.WeatherRepository.deleteWeathersForLocationId
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
        GlobalScope.launch {
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
                if (savedWidget.locationId != locationId) {
                    deleteWeathersAndForecastsForUnusedLocationId(savedWidget.locationId)
                }
                widgetDao.update(updatedWidget)
            } else {
                val newWidget = Widget(widgetId, false, appContext.applicationInfo.theme, locationId)
                widgetDao.insert(newWidget)
            }
            return@runBlocking true
        }
    }

    private fun deleteWeathersAndForecastsForUnusedLocationId(locationId: Int) {
        if (!isLocationUsed(locationId)) {
            deleteWeathersForLocationId(locationId)
            deleteForecastsForLocationId(locationId)
        }
    }

    @Transaction
    fun changeWidgetTextBold(widgetId: Int) {
        GlobalScope.launch {
            val savedWidget = widgetDao.getById(widgetId)
            if (savedWidget != null) {
                val updatedWidget = savedWidget.copy(isBold = !savedWidget.isBold)
                widgetDao.update(updatedWidget)
            }
        }
    }

    @Transaction
    fun changeWidgetTheme(widgetId: Int, themeId: Int) {
        GlobalScope.launch {
            val savedWidget = widgetDao.getById(widgetId)
            if (savedWidget != null) {
                val updatedWidget = savedWidget.copy(themeId = themeId)
                widgetDao.update(updatedWidget)
            }
        }
    }
}