package by.rudkouski.widget.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import by.rudkouski.widget.entity.Location

@Dao
interface LocationDao {

    @Query("SELECT * FROM locations")
    suspend fun getAll(): List<Location>

    @Query(
        "SELECT location_id, location_name_code, location_longitude, location_latitude, location_zone FROM locations, widgets WHERE location_id = widget_location_id")
    suspend fun getAllUsed(): List<Location>?

    @Query("SELECT * FROM locations WHERE location_id = :locationId")
    suspend fun getById(locationId: Int): Location

    @Query(
        "SELECT location_id, location_name_code, location_longitude, location_latitude, location_zone FROM locations, widgets WHERE location_id = widget_location_id AND widget_id =:widgetId")
    suspend fun getByWidgetId(widgetId: Int): Location?

    @Insert
    suspend fun insert(location: Location)

    @Update
    suspend fun update(location: Location)

    @Query(
        "SELECT location_id, location_name_code, location_longitude, location_latitude, location_zone FROM locations, widgets WHERE location_id = widget_location_id AND widget_location_id =:locationId")
    suspend fun getUsed(locationId: Int): List<Location>?
}