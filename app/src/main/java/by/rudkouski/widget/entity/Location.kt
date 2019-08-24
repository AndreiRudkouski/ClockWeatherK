package by.rudkouski.widget.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import by.rudkouski.widget.app.App
import by.rudkouski.widget.database.converter.TimeZoneConverter
import by.rudkouski.widget.update.listener.LocationChangeListener
import java.util.*

@Entity(tableName = "locations")
@TypeConverters(TimeZoneConverter::class)
data class Location(@PrimaryKey
                    @ColumnInfo(name = "location_id")
                    val id: Int,
                    @ColumnInfo(name = "location_name_code")
                    val name_code: String,
                    @ColumnInfo(name = "location_latitude")
                    val latitude: Double,
                    @ColumnInfo(name = "location_longitude")
                    val longitude: Double,
                    @ColumnInfo(name = "location_timeZone")
                    val timeZone: TimeZone) {

    companion object {
        const val CURRENT_LOCATION_ID = 1
        const val CURRENT_LOCATION = "current_location"
        private const val DEFAULT_LOCATION = "default_location"
    }

    fun getName(): String {
        return when {
            name_code == CURRENT_LOCATION || (id == CURRENT_LOCATION_ID && LocationChangeListener.isPermissionsDenied()) -> getNameByCode(DEFAULT_LOCATION)
            id != CURRENT_LOCATION_ID -> getNameByCode(name_code)
            else -> name_code
        }
    }

    private fun getNameByCode(nameCode: String): String {
        return App.appContext.getString(App.appContext.resources.getIdentifier(nameCode, "string", App.appContext.packageName))
    }
}