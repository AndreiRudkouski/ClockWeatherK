package by.rudkouski.widget.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import by.rudkouski.widget.entity.Forecast

@Dao
interface ForecastDao {

    @Query("SELECT * FROM forecasts WHERE forecast_id = :forecastId")
    suspend fun getById(forecastId: Int): Forecast?

    @Query("SELECT * FROM forecasts WHERE forecast_location_id =:locationId")
    suspend fun getAllByLocationId(locationId: Int): List<Forecast>?

    @Insert
    suspend fun insertAll(forecasts: List<Forecast>)

    @Query("DELETE FROM forecasts WHERE forecast_location_id =:locationId")
    suspend fun deleteAllForLocationId(locationId: Int)
}