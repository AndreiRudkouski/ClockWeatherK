package by.rudkouski.widget.database.dao

import androidx.room.*
import by.rudkouski.widget.entity.Widget

@Dao
interface WidgetDao {

    @Transaction
    @Query("SELECT * FROM widgets WHERE widget_id = :widgetId")
    suspend fun getById(widgetId: Int): Widget?

    @Insert
    suspend fun insert(widget: Widget)

    @Update
    suspend fun update(widget: Widget)

    @Delete
    suspend fun delete(widget: Widget)
}