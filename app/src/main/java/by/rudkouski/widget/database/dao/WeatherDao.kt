package by.rudkouski.widget.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import by.rudkouski.widget.entity.Weather

@Dao
interface WeatherDao {

    @Query("SELECT * FROM weathers WHERE weather_location_id =:locationId AND weather_type = :type")
    suspend fun getAllByLocationIdAndType(locationId: Int, type: String): List<Weather>?

    @Insert
    suspend fun insert(weather: Weather)

    @Insert
    suspend fun insertAll(weathers: List<Weather>)

    @Update
    suspend fun update(weather: Weather)

    @Query("DELETE FROM weathers WHERE weather_location_id =:locationId AND weather_type = :type")
    suspend fun deleteAllForLocationIdAndType(locationId: Int, type: String)
}