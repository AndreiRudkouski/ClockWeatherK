package by.rudkouski.widget.database.dao

import androidx.room.*
import by.rudkouski.widget.entity.Location

@Dao
interface LocationDao {

    @Transaction
    @Query("SELECT * FROM locations")
    suspend fun getAll(): List<Location>

    @Transaction
    @Query(
        "SELECT location_id, location_name_code, location_longitude, location_latitude, location_zone FROM locations, widgets WHERE location_id = widget_location_id")
    suspend fun getAllUsed(): List<Location>?

    @Transaction
    @Query("SELECT * FROM locations WHERE location_id = :locationId")
    suspend fun getById(locationId: Int): Location

    @Transaction
    @Query(
        "SELECT location_id, location_name_code, location_longitude, location_latitude, location_zone FROM locations, widgets WHERE location_id = widget_location_id AND widget_id =:widgetId")
    suspend fun getByWidgetId(widgetId: Int): Location?

    @Insert
    suspend fun insert(location: Location)

    @Update
    suspend fun update(location: Location)

    @Transaction
    @Query(
        "SELECT location_id, location_name_code, location_longitude, location_latitude, location_zone FROM locations, widgets WHERE location_id = widget_location_id AND widget_location_id =:locationId")
    suspend fun getUsed(locationId: Int): Location?
}