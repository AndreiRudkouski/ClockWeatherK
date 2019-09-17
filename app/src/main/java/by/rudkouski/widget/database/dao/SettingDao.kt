package by.rudkouski.widget.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import by.rudkouski.widget.entity.Setting

@Dao
interface SettingDao {

    @Transaction
    @Query("SELECT * FROM settings WHERE setting_widget_id = :widgetId")
    suspend fun getPrivateByWidgetId(widgetId: Int): List<Setting>?

    @Transaction
    @Query("SELECT * FROM settings WHERE setting_widget_id = :widgetId OR setting_widget_id IS NULL")
    suspend fun getAllByWidgetId(widgetId: Int): List<Setting>?

    @Transaction
    @Query("SELECT * FROM settings WHERE setting_id = :settingId")
    suspend fun getById(settingId: Int): Setting?

    @Transaction
    @Query("SELECT * FROM settings")
    suspend fun getAll(): List<Setting>?

    @Transaction
    @Query("SELECT * FROM settings WHERE setting_code = :code")
    suspend fun getByCode(code: String): Setting?

    @Insert
    suspend fun insert(setting: Setting)

    @Transaction
    @Query("UPDATE settings SET setting_value = :value WHERE setting_id = :settingId")
    suspend fun update(settingId: Int, value: Int)
}