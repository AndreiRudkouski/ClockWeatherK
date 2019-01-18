package by.rudkouski.clockWeatherK.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.*
import by.rudkouski.clockWeatherK.R
import by.rudkouski.clockWeatherK.app.App
import by.rudkouski.clockWeatherK.app.App.Companion.appContext
import by.rudkouski.clockWeatherK.entity.Forecast
import by.rudkouski.clockWeatherK.entity.Location
import by.rudkouski.clockWeatherK.entity.Location.Companion.CURRENT_LOCATION_ID
import by.rudkouski.clockWeatherK.entity.Weather
import by.rudkouski.clockWeatherK.entity.Widget
import java.util.*
import kotlin.Int.Companion.MIN_VALUE
import kotlin.collections.ArrayList

class DBHelper private constructor(context: Context, dbName: String, factory: SQLiteDatabase.CursorFactory,
                                   dbVersion: Int) : SQLiteOpenHelper(context, dbName, factory, dbVersion) {

    private val database: SQLiteDatabase = writableDatabase

    companion object {
        private const val DATABASE_VERSION: Int = 1
        private const val DATABASE_NAME: String = "clock_weather"
        val INSTANCE = DBHelper(App.appContext, DATABASE_NAME, Factory(), DATABASE_VERSION)

        private const val FORECAST_TABLE = "forecasts"
        private const val FORECAST_ID = "forecast_id"
        private const val FORECAST_CODE = "forecast_code"
        private const val FORECAST_DATE = "forecast_date"
        private const val FORECAST_HIGH_TEMP = "forecast_high_temp"
        private const val FORECAST_LOW_TEMP = "forecast_low_temp"
        private const val FORECAST_LOCATION_ID = "forecast_location_id"
        private const val LOCATION_TABLE = "locations"
        private const val LOCATION_ID = "location_id"
        private const val LOCATION_LATITUDE = "location_latitude"
        private const val LOCATION_LONGITUDE = "location_longitude"
        private const val LOCATION_NAME_CODE = "location_name_code"
        private const val LOCATION_TIME_ZONE = "location_time_zone"
        private const val WIDGET_TABLE = "widgets"
        private const val WIDGET_ID = "widget_id"
        private const val WIDGET_BOLD = "widget_bold"
        private const val WIDGET_LOCATION_ID = "widget_location_id"
        private const val WEATHER_TABLE = "weathers"
        private const val WEATHER_ID = "weather_id"
        private const val WEATHER_DATE = "weather_date"
        private const val WEATHER_DESCRIPTION = "weather_description"
        private const val WEATHER_ICON = "weather_icon"
        private const val WEATHER_PRECIPITATION_INTENSITY = "weather_precipitation_intensity"
        private const val WEATHER_PRECIPITATION_PROBABILITY = "weather_precipitation_probability"
        private const val WEATHER_TEMP = "weather_temp"
        private const val WEATHER_APPARENT_TEMP = "weather_apparent_temperature"
        private const val WEATHER_DEW_POINT = "weather_dew_point"
        private const val WEATHER_HUMIDITY = "weather_humidity"
        private const val WEATHER_PRESSURE = "weather_pressure"
        private const val WEATHER_WIND_SPEED = "weather_wind_speed"
        private const val WEATHER_WIND_GUST = "weather_wind_gust"
        private const val WEATHER_WIND_DIRECTION = "weather_wind_direction"
        private const val WEATHER_CLOUD_COVER = "weather_cloud_cover"
        private const val WEATHER_UV_INDEX = "weather_uv_index"
        private const val WEATHER_VISIBILITY = "weather_visibility"
        private const val WEATHER_OZONE = "weather_ozone"
        private const val WEATHER_LOCATION_ID = "weather_location_id"

        private const val IS_EQUAL_PARAMETER = " = ?"
        private const val DROP_TABLE_IF_EXISTS: String = "DROP TABLE IF EXISTS "

        private const val CURRENT_LOCATION = "current_location"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("PRAGMA foreign_keys = 'ON'")
        db.execSQL("CREATE TABLE IF NOT EXISTS " + LOCATION_TABLE + " (" + LOCATION_ID + " INTEGER PRIMARY KEY, " +
            LOCATION_LATITUDE + " DOUBLE, " + LOCATION_LONGITUDE + " DOUBLE, " + LOCATION_NAME_CODE + " TEXT, " +
            LOCATION_TIME_ZONE + " TEXT);")
        db.execSQL("CREATE TABLE IF NOT EXISTS " + WIDGET_TABLE + " (" + WIDGET_ID + " INTEGER PRIMARY KEY, " +
            WIDGET_BOLD + " INTEGER, " + WIDGET_LOCATION_ID + " INTEGER, FOREIGN KEY (" + WIDGET_LOCATION_ID + ") REFERENCES " +
            LOCATION_TABLE + " (" + LOCATION_ID + ") ON DELETE CASCADE);")
        db.execSQL("CREATE TABLE IF NOT EXISTS " + WEATHER_TABLE + " (" + WEATHER_ID + " INTEGER PRIMARY KEY, " +
            WEATHER_DATE + " INTEGER, " + WEATHER_DESCRIPTION + " TEXT, " + WEATHER_ICON + " TEXT, " +
            WEATHER_PRECIPITATION_INTENSITY + " DOUBLE, " + WEATHER_PRECIPITATION_PROBABILITY + " DOUBLE, " +
            WEATHER_TEMP + " DOUBLE, " + WEATHER_APPARENT_TEMP + " DOUBLE, " + WEATHER_DEW_POINT + " DOUBLE, " +
            WEATHER_HUMIDITY + " DOUBLE, " + WEATHER_PRESSURE + " DOUBLE, " + WEATHER_WIND_SPEED + " DOUBLE, " +
            WEATHER_WIND_GUST + " DOUBLE, " + WEATHER_WIND_DIRECTION + " INTEGER, " + WEATHER_CLOUD_COVER + " DOUBLE, " +
            WEATHER_UV_INDEX + " INTEGER, " + WEATHER_VISIBILITY + " DOUBLE, " + WEATHER_OZONE + " DOUBLE, " +
            WEATHER_LOCATION_ID + " INTEGER, FOREIGN KEY (" + WEATHER_LOCATION_ID + ") REFERENCES " + LOCATION_TABLE +
            " (" + LOCATION_ID + ") ON DELETE CASCADE);")
        db.execSQL("CREATE TABLE IF NOT EXISTS " + FORECAST_TABLE + " (" + FORECAST_ID + " " + "INTEGER PRIMARY KEY, " +
            FORECAST_CODE + " INTEGER, " + FORECAST_DATE + " INTEGER, " + FORECAST_HIGH_TEMP + " INTEGER, " +
            FORECAST_LOW_TEMP + " INTEGER, " + FORECAST_LOCATION_ID + " INTEGER, FOREIGN KEY" + " (" + FORECAST_LOCATION_ID + ") " +
            "REFERENCES " + LOCATION_TABLE + " (" + LOCATION_ID + ") ON DELETE CASCADE);")
        addDefaultLocations(db)
    }

    private fun addDefaultLocations(db: SQLiteDatabase) {
        val defaultLocations: Array<String> = App.appContext.resources.getStringArray(R.array.default_locations)
        for (defaultLocation in defaultLocations) {
            val location = defaultLocation.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            with(ContentValues()) {
                put(LOCATION_NAME_CODE, location[0])
                put(LOCATION_LATITUDE, location[1])
                put(LOCATION_LONGITUDE, location[2])
                put(LOCATION_TIME_ZONE, location[3])
                db.insert(LOCATION_TABLE, null, this)
            }
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        with(db) {
            execSQL(DROP_TABLE_IF_EXISTS + LOCATION_TABLE)
            execSQL(DROP_TABLE_IF_EXISTS + WIDGET_TABLE)
            execSQL(DROP_TABLE_IF_EXISTS + WEATHER_TABLE)
            execSQL(DROP_TABLE_IF_EXISTS + FORECAST_TABLE)
            onCreate(this)
        }
    }

    fun getAllLocations(): List<Location> {
        val locations = ArrayList<Location>()
        database.query(LOCATION_TABLE, null, null, null, null, null, null).use { cursor ->
            if (cursor.moveToFirst()) {
                for (i in 0 until cursor.count) {
                    if (cursor.moveToPosition(i)) {
                        val location = createLocation(cursor)
                        locations.add(location)
                    }
                }
            }
        }
        return locations
    }

    fun getLocationById(locationId: Int): Location {
        return getLocationFromDatabaseById(database, locationId)
    }

    fun updateCurrentLocation(locationName: String, latitude: Double, longitude: Double) {
        val values = ContentValues()
        values.put(LOCATION_NAME_CODE, locationName)
        values.put(LOCATION_LATITUDE, latitude)
        values.put(LOCATION_LONGITUDE, longitude)
        database.update(LOCATION_TABLE, values, LOCATION_ID + IS_EQUAL_PARAMETER,
            arrayOf(Location.CURRENT_LOCATION_ID.toString()))
    }

    fun isCurrentLocationNotUpdated() =
        getLocationById(Location.CURRENT_LOCATION_ID).name == appContext.getString(R.string.default_location)

    private fun getLocationFromDatabaseById(db: SQLiteDatabase, locationId: Int): Location {
        db.query(LOCATION_TABLE, null, LOCATION_ID + IS_EQUAL_PARAMETER, arrayOf(locationId.toString()), null, null,
            null).use { cursor ->
            if (cursor.moveToFirst()) {
                return createLocation(cursor)
            }
        }
        throw RuntimeException("An error occurred while getting the location with id = $locationId")
    }

    private fun createLocation(cursor: Cursor): Location {
        val id = cursor.getInt(cursor.getColumnIndexOrThrow(LOCATION_ID))
        val nameCode = cursor.getString(cursor.getColumnIndexOrThrow(LOCATION_NAME_CODE))
        val latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(LOCATION_LATITUDE))
        val longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(LOCATION_LONGITUDE))
        return if (id != CURRENT_LOCATION_ID) {
            val timeZone = TimeZone.getTimeZone(cursor.getString(cursor.getColumnIndexOrThrow(LOCATION_TIME_ZONE)))
            Location(id, nameCode, latitude, longitude, timeZone)
        } else {
            Location.createCurrentLocation(
                if (nameCode == CURRENT_LOCATION) appContext.getString(R.string.default_location)
                else nameCode, latitude, longitude)
        }
    }

    fun getLocationIdsContainedInAllWidgets(): List<Int> {
        return getLocationIdsForAllWidgetsFromDatabase(database)
    }

    fun getLocationByWidgetId(widgetId: Int): Int {
        val widget = getWidgetFromDatabase(database, widgetId)
        return widget?.location?.id ?: MIN_VALUE
    }

    private fun getLocationIdsForAllWidgetsFromDatabase(db: SQLiteDatabase): List<Int> {
        val locationIds = ArrayList<Int>()
        db.query(true, WIDGET_TABLE, arrayOf(WIDGET_LOCATION_ID), null, null, null, null, null, null).use { cursor ->
            if (cursor.moveToFirst()) {
                for (i in 0 until cursor.count) {
                    if (cursor.moveToPosition(i)) {
                        locationIds.add(cursor.getInt(cursor.getColumnIndexOrThrow(WIDGET_LOCATION_ID)))
                    }
                }
            }
        }
        return locationIds
    }

    fun getWidgetById(widgetId: Int): Widget {
        val widget = getWidgetFromDatabase(database, widgetId)
        return widget ?: throw RuntimeException("An error occurred while getting the widget with id = $widgetId")
    }

    private fun getWidgetFromDatabase(db: SQLiteDatabase, widgetId: Int): Widget? {
        db.query(WIDGET_TABLE, null, WIDGET_ID + IS_EQUAL_PARAMETER, arrayOf(widgetId.toString()), null, null, null)
            .use { cursor ->
                if (cursor.moveToFirst()) {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(WIDGET_ID))
                    val isBold = cursor.getInt(cursor.getColumnIndexOrThrow(WIDGET_BOLD)) != 0
                    val location =
                        getLocationFromDatabaseById(db, cursor.getInt(cursor.getColumnIndexOrThrow(WIDGET_LOCATION_ID)))
                    return Widget(id, location, isBold)
                }
            }
        return null
    }

    fun deleteWidgetById(widgetId: Int) {
        database.beginTransaction()
        try {
            val widget = getWidgetFromDatabase(database, widgetId)
            if (widget != null) {
                database.delete(WIDGET_TABLE, WIDGET_ID + IS_EQUAL_PARAMETER, arrayOf(widgetId.toString()))
                val oldLocationId = widget.location.id
                deleteWeatherAndForecastForLocationWithoutWidget(database, oldLocationId)
                database.setTransactionSuccessful()
            }
        } finally {
            database.endTransaction()
        }
    }

    fun setWeatherByLocationId(newWeather: Weather, locationId: Int) {
        database.beginTransaction()
        try {
            val existedWeather = getWeatherFromDatabase(database, locationId)
            if (existedWeather != null) {
                updateWeather(database, Weather(existedWeather.id, newWeather))
            } else {
                addWeather(database, newWeather, locationId)
            }
            database.setTransactionSuccessful()
        } finally {
            database.endTransaction()
        }
    }

    fun getWeatherByLocationId(locationId: Int): Weather? {
        return getWeatherFromDatabase(database, locationId)
    }

    private fun getWeatherFromDatabase(db: SQLiteDatabase, locationId: Int): Weather? {
        db.query(WEATHER_TABLE, null, WEATHER_LOCATION_ID + IS_EQUAL_PARAMETER, arrayOf(locationId.toString()), null,
            null, null)
            .use { cursor ->
                if (cursor.moveToFirst()) {
                    return createWeather(cursor)
                }
            }
        return null
    }

    private fun createWeather(cursor: Cursor): Weather {
        val id = cursor.getInt(cursor.getColumnIndexOrThrow(WEATHER_ID))
        val date = Date(cursor.getLong(cursor.getColumnIndexOrThrow(WEATHER_DATE)))
        val description = cursor.getString(cursor.getColumnIndexOrThrow(WEATHER_DESCRIPTION))
        val icon = cursor.getString(cursor.getColumnIndexOrThrow(WEATHER_ICON))
        val precipitationIntensity = cursor.getDouble(cursor.getColumnIndexOrThrow(WEATHER_PRECIPITATION_INTENSITY))
        val precipitationProbability = cursor.getDouble(cursor.getColumnIndexOrThrow(WEATHER_PRECIPITATION_PROBABILITY))
        val temp = cursor.getDouble(cursor.getColumnIndexOrThrow(WEATHER_TEMP))
        val apparentTemperature = cursor.getDouble(cursor.getColumnIndexOrThrow(WEATHER_APPARENT_TEMP))
        val dewPoint = cursor.getDouble(cursor.getColumnIndexOrThrow(WEATHER_DEW_POINT))
        val humidity = cursor.getDouble(cursor.getColumnIndexOrThrow(WEATHER_HUMIDITY))
        val pressure = cursor.getDouble(cursor.getColumnIndexOrThrow(WEATHER_PRESSURE))
        val windSpeed = cursor.getDouble(cursor.getColumnIndexOrThrow(WEATHER_WIND_SPEED))
        val windGust = cursor.getDouble(cursor.getColumnIndexOrThrow(WEATHER_WIND_GUST))
        val windDirection = cursor.getInt(cursor.getColumnIndexOrThrow(WEATHER_WIND_DIRECTION))
        val cloudCover = cursor.getDouble(cursor.getColumnIndexOrThrow(WEATHER_CLOUD_COVER))
        val uvIndex = cursor.getInt(cursor.getColumnIndexOrThrow(WEATHER_UV_INDEX))
        val visibility = cursor.getDouble(cursor.getColumnIndexOrThrow(WEATHER_VISIBILITY))
        val ozone = cursor.getDouble(cursor.getColumnIndexOrThrow(WEATHER_OZONE))
        return Weather(id, date, description, icon, precipitationIntensity, precipitationProbability, temp,
            apparentTemperature, dewPoint, humidity, pressure, windSpeed, windGust, windDirection, cloudCover, uvIndex,
            visibility, ozone)
    }

    private fun updateWeather(db: SQLiteDatabase, weather: Weather) {
        val values = createContentValues(weather)
        db.update(WEATHER_TABLE, values, WEATHER_ID + IS_EQUAL_PARAMETER, arrayOf(weather.id.toString()))

    }

    private fun addWeather(db: SQLiteDatabase, weather: Weather, locationId: Int) {
        val values = createContentValues(weather)
        values.put(WEATHER_LOCATION_ID, locationId)
        db.insert(WEATHER_TABLE, null, values)
    }

    private fun createContentValues(weather: Weather): ContentValues {
        with(ContentValues()) {
            put(WEATHER_DATE, weather.date.time)
            put(WEATHER_DESCRIPTION, weather.description)
            put(WEATHER_ICON, weather.iconName)
            put(WEATHER_PRECIPITATION_INTENSITY, weather.precipitationIntensity)
            put(WEATHER_PRECIPITATION_PROBABILITY, weather.precipitationProbability)
            put(WEATHER_TEMP, weather.temperature)
            put(WEATHER_APPARENT_TEMP, weather.apparentTemperature)
            put(WEATHER_DEW_POINT, weather.dewPoint)
            put(WEATHER_HUMIDITY, weather.humidity)
            put(WEATHER_PRESSURE, weather.pressure)
            put(WEATHER_WIND_SPEED, weather.windSpeed)
            put(WEATHER_WIND_GUST, weather.windGust)
            put(WEATHER_WIND_DIRECTION, weather.windDirection)
            put(WEATHER_CLOUD_COVER, weather.cloudCover)
            put(WEATHER_UV_INDEX, weather.uvIndex)
            put(WEATHER_VISIBILITY, weather.visibility)
            put(WEATHER_OZONE, weather.ozone)
            return this
        }
    }

    fun getForecastsByLocationId(locationId: Int): List<Forecast> {
        return getForecastsFromDatabase(database, locationId)
    }

    fun setForecastsByLocationId(newForecasts: List<Forecast>, locationId: Int) {
        database.beginTransaction()
        try {
            val existedForecasts = getForecastsFromDatabase(database, locationId)
            if (existedForecasts.isEmpty()) {
                addForecasts(database, newForecasts, locationId)
            } else {
                val forecasts = ArrayList<Forecast>(newForecasts)
                for (i in existedForecasts.indices) {
                    val existForecastId = existedForecasts[i].id
                    forecasts.add(Forecast(existForecastId, newForecasts[i]))
                }
                updateForecasts(database, forecasts)
            }
            database.setTransactionSuccessful()
        } finally {
            database.endTransaction()
        }
    }

    private fun getForecastsFromDatabase(db: SQLiteDatabase, locationId: Int): List<Forecast> {
        val forecasts = ArrayList<Forecast>()
        db.query(FORECAST_TABLE,
            null, FORECAST_LOCATION_ID + IS_EQUAL_PARAMETER, arrayOf(locationId.toString()), null, null,
            FORECAST_DATE).use { cursor ->
            if (cursor.moveToFirst()) {
                for (i in 0 until cursor.count) {
                    if (cursor.moveToPosition(i)) {
                        forecasts.add(createForecast(cursor))
                    }
                }
            }
        }
        return forecasts
    }


    private fun updateForecasts(db: SQLiteDatabase, forecasts: List<Forecast>) {
        for (forecast in forecasts) {
            val values = createContentValues(forecast)
            db.update(FORECAST_TABLE, values, FORECAST_ID + IS_EQUAL_PARAMETER, arrayOf(forecast.id.toString()))
        }
    }

    private fun addForecasts(database: SQLiteDatabase, forecasts: List<Forecast>, locationId: Int) {
        for (forecast in forecasts) {
            val values = createContentValues(forecast)
            values.put(FORECAST_LOCATION_ID, locationId)
            database.insert(FORECAST_TABLE, null, values)
        }
    }

    private fun createForecast(cursor: Cursor): Forecast {
        val id = cursor.getInt(cursor.getColumnIndexOrThrow(FORECAST_ID))
        val code = cursor.getInt(cursor.getColumnIndexOrThrow(FORECAST_CODE))
        val date = Date(cursor.getLong(cursor.getColumnIndexOrThrow(FORECAST_DATE)))
        val highTemp = cursor.getInt(cursor.getColumnIndexOrThrow(FORECAST_HIGH_TEMP))
        val lowTemp = cursor.getInt(cursor.getColumnIndexOrThrow(FORECAST_LOW_TEMP))
        return Forecast(id, code, date, highTemp, lowTemp)
    }

    private fun createContentValues(forecast: Forecast): ContentValues {
        with(ContentValues()) {
            put(FORECAST_CODE, forecast.code)
            put(FORECAST_DATE, forecast.date.time)
            put(FORECAST_HIGH_TEMP, forecast.highTemp)
            put(FORECAST_LOW_TEMP, forecast.lowTemp)
            return this
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
                val newWidget = Widget(widgetId, location, existedWidget.isBold)
                if (existedWidget.location.id != locationId) {
                    updateWidget(database, newWidget)
                    deleteWeatherAndForecastForLocationWithoutWidget(database, existedWidget.location.id)
                    database.setTransactionSuccessful()
                }
            } else {
                val newWidget = Widget(widgetId, location, false)
                addWidget(database, newWidget)
                database.setTransactionSuccessful()
            }
            return true
        } finally {
            database.endTransaction()
        }
    }

    private fun addWidget(db: SQLiteDatabase, widget: Widget) {
        val values = createValuesForWidget(widget)
        values.put(WIDGET_ID, widget.id)
        db.insert(WIDGET_TABLE, null, values)
    }

    private fun updateWidget(db: SQLiteDatabase, widget: Widget) {
        val values = createValuesForWidget(widget)
        db.update(WIDGET_TABLE, values, WIDGET_ID + IS_EQUAL_PARAMETER, arrayOf((widget.id.toString())))
    }

    private fun createValuesForWidget(widget: Widget): ContentValues {
        with(ContentValues()) {
            put(WIDGET_LOCATION_ID, widget.location.id)
            put(WIDGET_BOLD, if (widget.isBold) 1 else 0)
            return this
        }
    }

    private fun deleteWeatherAndForecastForLocationWithoutWidget(db: SQLiteDatabase, locationId: Int) {
        if (!getLocationIdsForAllWidgetsFromDatabase(db).contains(locationId)) {
            db.delete(WEATHER_TABLE, WEATHER_LOCATION_ID + IS_EQUAL_PARAMETER, arrayOf(locationId.toString()))
            db.delete(FORECAST_TABLE, FORECAST_LOCATION_ID + IS_EQUAL_PARAMETER, arrayOf(locationId.toString()))
        }
    }

    fun changeWidgetTextBold(widgetId: Int) {
        database.beginTransaction()
        try {
            val existWidget = getWidgetFromDatabase(database, widgetId)
            if (existWidget != null) {
                val newWidget = Widget(widgetId, existWidget.location, !existWidget.isBold)
                updateWidget(database, newWidget)
                database.setTransactionSuccessful()
            }
        } finally {
            database.endTransaction()
        }
    }

    class Factory : SQLiteDatabase.CursorFactory {
        override fun newCursor(db: SQLiteDatabase?, masterQuery: SQLiteCursorDriver?, editTable: String?,
                               query: SQLiteQuery?): Cursor = SQLiteCursor(masterQuery, editTable, query)
    }
}