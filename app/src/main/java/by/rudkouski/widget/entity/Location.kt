package by.rudkouski.widget.entity

import android.content.Context
import by.rudkouski.widget.app.App
import java.util.*

class Location {

    val id: Int
    val name: String
    val latitude: Double
    val longitude: Double
    val timeZone: TimeZone

    constructor(id: Int, nameCode: String, latitude: Double, longitude: Double, timeZone: TimeZone) {
        this.id = id
        this.latitude = latitude
        this.longitude = longitude
        this.timeZone = timeZone
        this.name = getNameByCode(nameCode, App.appContext)
    }

    private constructor(name: String, latitude: Double, longitude: Double) {
        this.id = CURRENT_LOCATION_ID
        this.name = name
        this.latitude = latitude
        this.longitude = longitude
        this.timeZone = TimeZone.getDefault()
    }

    companion object {
        const val CURRENT_LOCATION_ID = 1

        fun createCurrentLocation(name: String, latitude: Double, longitude: Double): Location {
            return Location(name, latitude, longitude)
        }
    }

    private fun getNameByCode(nameCode: String, context: Context): String {
        return context.getString(
            context.resources.getIdentifier(nameCode, "string", context.packageName))
    }
}