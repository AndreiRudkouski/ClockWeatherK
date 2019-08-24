package by.rudkouski.widget.repository

import androidx.room.Transaction
import by.rudkouski.widget.app.App.Companion.appContext
import by.rudkouski.widget.database.AppDatabase.Companion.INSTANCE
import by.rudkouski.widget.entity.Widget
import by.rudkouski.widget.repository.LocationRepository.isLocationUsed
import by.rudkouski.widget.repository.WeatherRepository.deleteWeatherForLocationId
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
                deleteWeatherForUnusedLocationId(savedWidget.locationId)
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
                    deleteWeatherForUnusedLocationId(savedWidget.locationId)
                }
                widgetDao.update(updatedWidget)
            } else {
                val newWidget = Widget(widgetId, false, appContext.applicationInfo.theme, locationId)
                widgetDao.insert(newWidget)
            }
            return@runBlocking true
        }
    }

    private fun deleteWeatherForUnusedLocationId(locationId: Int) {
        if (!isLocationUsed(locationId)) {
            deleteWeatherForLocationId(locationId)
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


    /* fun getWidgetById(widgetId: Int): Widget? {
        return getWidgetFromDatabase(database, widgetId)
    }

    fun deleteWidgetById(widgetId: Int) {
        database.beginTransaction()
        try {
            val widget = getWidgetFromDatabase(database, widgetId)
            if (widget != null) {
                database.delete(
                    DBHelper.WIDGET_TABLE, DBHelper.WIDGET_ID + DBHelper.IS_EQUAL_PARAMETER, arrayOf(widgetId.toString()))
                val oldLocationId = widget.location.id
                deleteWeatherForLocationWithoutWidget(database, oldLocationId)
                database.setTransactionSuccessful()
            }
        } finally {
            database.endTransaction()
        }
    }

    fun setWidgetById(widgetId: Int, locationId: Int): Boolean {
        database.beginTransaction()
        try {
            val existedWidget = getWidgetFromDatabase(database, widgetId)
            val location = getLocationFromDatabaseById(database, locationId)
            if (existedWidget != null) {
                if (existedWidget.location.id == locationId) {
                    return false
                }
                val newWidget = Widget(widgetId, location, existedWidget.isBold, existedWidget.themeId)
                if (existedWidget.location.id != locationId) {
                    updateWidget(database, newWidget)
                    deleteWeatherForLocationWithoutWidget(database, existedWidget.location.id)
                    database.setTransactionSuccessful()
                }
            } else {
                val newWidget = Widget(widgetId, location, false, App.appContext.applicationInfo.theme)
                addWidget(database, newWidget)
                database.setTransactionSuccessful()
            }
            return true
        } finally {
            database.endTransaction()
        }
    }



    fun changeWidgetTextBold(widgetId: Int) {
        database.beginTransaction()
        try {
            val existWidget = getWidgetFromDatabase(database, widgetId)
            if (existWidget != null) {
                val newWidget = Widget(widgetId, existWidget.location, !existWidget.isBold, existWidget.themeId)
                updateWidget(database, newWidget)
                database.setTransactionSuccessful()
            }
        } finally {
            database.endTransaction()
        }
    }

    fun changeWidgetTheme(widgetId: Int, themeId: Int) {
        database.beginTransaction()
        try {
            val existWidget = getWidgetFromDatabase(database, widgetId)
            if (existWidget != null) {
                val newWidget = Widget(widgetId, existWidget.location, existWidget.isBold, themeId)
                updateWidget(database, newWidget)
                database.setTransactionSuccessful()
            }
        } finally {
            database.endTransaction()
        }
    }

    fun getWidgetIdByForecastId(forecastId: Int): Int {
        val query = "SELECT * FROM " + DBHelper.WIDGET_TABLE + " INNER JOIN " + DBHelper.FORECAST_TABLE + " ON " +
            DBHelper.WIDGET_LOCATION_ID + " = " + DBHelper.FORECAST_LOCATION_ID + " WHERE " + DBHelper.FORECAST_ID + DBHelper.IS_EQUAL_PARAMETER
        database.rawQuery(query, arrayOf(forecastId.toString())).use { cursor ->
            if (cursor.moveToFirst()) {
                for (i in 0 until cursor.count) {
                    if (cursor.moveToPosition(i)) {
                        return cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.WIDGET_ID))
                    }
                }
            }
        }
        return 0
    }*/
}