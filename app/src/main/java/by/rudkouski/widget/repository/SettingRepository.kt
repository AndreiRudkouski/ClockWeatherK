package by.rudkouski.widget.repository

import androidx.room.Transaction
import by.rudkouski.widget.R
import by.rudkouski.widget.app.App.Companion.appContext
import by.rudkouski.widget.database.AppDatabase
import by.rudkouski.widget.entity.Setting
import kotlinx.coroutines.runBlocking
import java.util.Locale.getDefault

object SettingRepository {

    private val settingDao = AppDatabase.INSTANCE.settingDao()

    fun getSettingsByWidgetId(widgetId: Int): List<Setting>? {
        return runBlocking {
            settingDao.getAllByWidgetId(widgetId)
        }
    }

    @Transaction
    fun updateSettingValueById(settingId: Int, value: Int): Setting? {
        return runBlocking {
            settingDao.update(settingId, value)
            return@runBlocking settingDao.getById(settingId)
        }
    }

    @Transaction
    suspend fun setDefaultSettingsByWidgetId(widgetId: Int) {
        val defaultSettings: Array<String> = appContext.resources.getStringArray(R.array.default_settings)
        for (i in defaultSettings.indices) {
            val settingData = defaultSettings[i].split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val code = Setting.Code.valueOf(settingData[0].toUpperCase(getDefault()))
            val value = settingData[1].toInt()
            val type = Setting.Type.valueOf(settingData[2].toUpperCase(getDefault()))
            val setting = Setting(code, value, type, widgetId)
            settingDao.insert(setting)
        }
    }
}