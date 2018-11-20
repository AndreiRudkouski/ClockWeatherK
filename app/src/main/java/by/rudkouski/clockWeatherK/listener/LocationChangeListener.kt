package by.rudkouski.clockWeatherK.listener

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.*
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.text.format.DateUtils.HOUR_IN_MILLIS
import by.rudkouski.clockWeatherK.R
import by.rudkouski.clockWeatherK.app.App
import by.rudkouski.clockWeatherK.entity.Location
import by.rudkouski.clockWeatherK.receiver.WeatherUpdateBroadcastReceiver
import java.util.*

@SuppressLint("MissingPermission")
object LocationChangeListener : LocationListener {

    fun startLocationUpdate() {
        if (isPermissionsGranted()) {
            val locationManager = App.appContext.getSystemService(LOCATION_SERVICE) as LocationManager
            val provider = locationManager.getBestProvider(Criteria(), true)
            locationManager.requestLocationUpdates(provider, HOUR_IN_MILLIS, 0f, this)
        }
    }

    fun isPermissionsGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(App.appContext, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(App.appContext, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
    }

    fun stopLocationUpdate() {
        if (isPermissionsGranted()) {
            val locationManager = App.appContext.getSystemService(LOCATION_SERVICE) as LocationManager
            locationManager.removeUpdates(this)
        }
    }

    fun getLocation(): Location {
        val address = getAddress()
        return if (address != null) {
            val locationName = if (address.locality != null) address.locality else address.subAdminArea
            Location.createCurrentLocation(locationName, address.latitude, address.longitude)
        } else {
            Location.createCurrentLocation(App.appContext.getString(R.string.default_location), 0.0, 0.0)
        }
    }

    private fun getAddress(): Address? {
        if (isPermissionsGranted()) {
            val locationManager = App.appContext.getSystemService(LOCATION_SERVICE) as LocationManager
            val provider = locationManager.getBestProvider(Criteria(), true)
            val location = locationManager.getLastKnownLocation(provider)
            if (location != null) {
                val longitude = location.longitude
                val latitude = location.latitude
                val geoCoder = Geocoder(App.appContext, Locale.getDefault())
                val addresses = geoCoder.getFromLocation(latitude, longitude, 1)
                if (addresses != null && addresses.size > 0) {
                    return addresses[0]
                }
            }
        }
        return null
    }

    override fun onLocationChanged(location: android.location.Location) {
        WeatherUpdateBroadcastReceiver.updateWeatherPendingIntent(App.appContext).send()
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
    }
}