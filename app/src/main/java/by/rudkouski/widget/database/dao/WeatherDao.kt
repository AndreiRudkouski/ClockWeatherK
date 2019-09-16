package by.rudkouski.widget.database.dao

import androidx.room.*
import by.rudkouski.widget.entity.Weather

@Dao
interface WeatherDao {

    @Transaction
    @Query("SELECT * FROM weathers WHERE weather_location_id =:locationId AND weather_type = :type")
    suspend fun getAllByLocationIdAndType(locationId: Int, type: String): List<Weather>?

    @Transaction
    @Query("SELECT * FROM weathers WHERE weather_id =:weatherId")
    suspend fun getById(weatherId: Int): Weather?

    @Insert
    suspend fun insert(weather: Weather)

    @Insert
    suspend fun insertAll(weathers: List<Weather>)

    @Update
    suspend fun update(weather: Weather)

    @Delete
    suspend fun delete(weather: Weather)

    @Transaction
    @Query("DELETE FROM weathers WHERE weather_location_id =:locationId AND weather_type = :type")
    suspend fun deleteAllForLocationIdAndType(locationId: Int, type: String)

    @Transaction
    @Query("DELETE FROM weathers WHERE weather_location_id =:locationId")
    suspend fun deleteAllForLocationId(locationId: Int)

    @Transaction
    @Query(
        "SELECT * FROM weathers WHERE weather_location_id =:locationId AND weather_type = :type AND datetime(weather_date) BETWEEN datetime(:timeFrom) AND datetime(:timeTo)")
    suspend fun getAllByParamsAndTimeInterval(locationId: Int, type: String, timeFrom: String, timeTo: String): List<Weather>?
}