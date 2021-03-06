package by.rudkouski.widget.database

import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import by.rudkouski.widget.R
import by.rudkouski.widget.app.App.Companion.appContext
import by.rudkouski.widget.database.dao.*
import by.rudkouski.widget.entity.*
import by.rudkouski.widget.entity.Location.Companion.CURRENT_LOCATION
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.threeten.bp.ZoneId.of
import org.threeten.bp.ZoneId.systemDefault
import java.util.*

@Database(entities = [Location::class, Widget::class, Weather::class, Forecast::class, Setting::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        private val callback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                initDefaultData()
            }
        }

        private fun initDefaultData() {
            GlobalScope.launch {
                initDefaultLocations()
                initDefaultSettings()
            }
        }

        private suspend fun initDefaultLocations() {
            val defaultLocations: Array<String> = appContext.resources.getStringArray(R.array.default_locations)
            for (i in defaultLocations.indices) {
                val locationData = defaultLocations[i].split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val code = if (locationData[0].isEmpty()) CURRENT_LOCATION else locationData[0]
                val latitude = locationData[1].toDouble()
                val longitude = locationData[2].toDouble()
                val zoneId = if (locationData.size == 4) of(locationData[3]) else systemDefault()
                val location = Location(i + 1, code, latitude, longitude, zoneId)
                INSTANCE.locationDao().insert(location)
            }
        }

        private suspend fun initDefaultSettings() {
            val defaultSettings: Array<String> = appContext.resources.getStringArray(R.array.default_common_settings)
            for (i in defaultSettings.indices) {
                val settingData = defaultSettings[i].split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val code = Setting.Code.valueOf(settingData[0].toUpperCase(Locale.getDefault()))
                val value = settingData[1].toInt()
                val type = Setting.Type.valueOf(settingData[2].toUpperCase(Locale.getDefault()))
                val setting = Setting(code, value, type, null)
                INSTANCE.settingDao().insert(setting)
            }
        }

        val INSTANCE = databaseBuilder(appContext, AppDatabase::class.java, "clock_weather_database").addCallback(callback).build()
    }

    abstract fun locationDao(): LocationDao

    abstract fun widgetDao(): WidgetDao

    abstract fun weatherDao(): WeatherDao

    abstract fun forecastDao(): ForecastDao

    abstract fun settingDao(): SettingDao
}