package by.rudkouski.widget.entity

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import by.rudkouski.widget.database.converter.ZoneIdConverter
import by.rudkouski.widget.update.receiver.LocationUpdateBroadcastReceiver.Companion.isPermissionsDenied
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneId.systemDefault

@Entity(tableName = "locations")
@TypeConverters(ZoneIdConverter::class)
data class Location(@PrimaryKey
                    @ColumnInfo(name = "location_id")
                    val id: Int,
                    @ColumnInfo(name = "location_name_code")
                    val name_code: String,
                    @ColumnInfo(name = "location_latitude")
                    val latitude: Double,
                    @ColumnInfo(name = "location_longitude")
                    val longitude: Double,
                    @ColumnInfo(name = "location_zone")
                    val zoneId: ZoneId) {

    companion object {
        const val CURRENT_LOCATION_ID = 1
        const val CURRENT_LOCATION = "current_location"
        val DEFAULT_LOCATION = Location(CURRENT_LOCATION_ID, CURRENT_LOCATION, 360.0, 360.0, systemDefault())
        private const val DEFAULT_LOCATION_NAME_CODE = "default_location"
    }

    fun getName(context: Context): String {
        return when {
            name_code == CURRENT_LOCATION || (id == CURRENT_LOCATION_ID && isPermissionsDenied()) ->
                getNameByCode(context, DEFAULT_LOCATION_NAME_CODE)
            id != CURRENT_LOCATION_ID -> getNameByCode(context, name_code)
            else -> name_code
        }
    }

    private fun getNameByCode(context: Context, nameCode: String): String {
        return context.getString(context.resources.getIdentifier(nameCode, "string", context.packageName))
    }
}