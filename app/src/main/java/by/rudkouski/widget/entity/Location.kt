package by.rudkouski.widget.entity

import java.util.*

class Location(val id: Int, val name: String, val latitude: Double, val longitude: Double, val timeZone: TimeZone) {

    companion object {
        const val CURRENT_LOCATION_ID = 1
    }
}