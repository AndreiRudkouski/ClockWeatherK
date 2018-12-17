package by.rudkouski.clockWeatherK.receiver

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.AlarmManager.INTERVAL_FIFTEEN_MINUTES
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Address
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import by.rudkouski.clockWeatherK.R
import by.rudkouski.clockWeatherK.app.App.Companion.appContext
import by.rudkouski.clockWeatherK.entity.Location
import by.rudkouski.clockWeatherK.provider.WidgetProvider
import java.io.IOException
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference


@SuppressLint("MissingPermission")
object LocationChangeChecker {

    private var location = AtomicReference<Location>()
    private val isRegistered = AtomicBoolean(false)
    private val locationManager = appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val locationListener = object : android.location.LocationListener {
        override fun onLocationChanged(lastLocation: android.location.Location?) {
            if (lastLocation != null) {
                if (location.get() == null) {
                    locationManager.removeUpdates(this)
                    setRequestLocationUpdates(INTERVAL_FIFTEEN_MINUTES, this)
                }
                setLocation(lastLocation)
            }
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String?) {
        }

        override fun onProviderDisabled(provider: String?) {
        }

    }

    fun startLocationUpdate() {
        if (isPermissionsGranted()) {
            if (location.get() == null) {
                setRequestLocationUpdates(0, locationListener)
            } else {
                setRequestLocationUpdates(INTERVAL_FIFTEEN_MINUTES, locationListener)
            }
        }
    }

    private fun setRequestLocationUpdates(period: Long, listener: LocationListener) {
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, period, 0f, listener)
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, period, 0f, listener)
    }

    fun stopLocationUpdate() {
        if (isRegistered.get()) {
            locationManager.removeUpdates(locationListener)
            isRegistered.set(false)
        }
    }

    fun isPermissionsGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(appContext, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(appContext, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
    }

    fun getLastLocation(): Location {
        return if (location.get() != null) location.get()
        else Location.createCurrentLocation(appContext.getString(R.string.default_location), 0.0, 0.0)
    }

    private fun setLocation(lastLocation: android.location.Location) {
        val address = getAddress(lastLocation)
        if (address != null && (address.locality != null || address.subAdminArea != null || address.adminArea != null)) {
            val locationName =
                when {
                    address.locality != null -> address.locality
                    address.subAdminArea != null -> address.subAdminArea
                    else -> address.adminArea
                }
            val newLocation = Location.createCurrentLocation(locationName, address.latitude, address.longitude)
            if (location.get() == null) {
                location.set(newLocation)
                sendIntentToWidgetUpdate()
            } else {
                location.set(newLocation)
            }
        }
    }

    private fun getAddress(location: android.location.Location): Address? {
        val longitude = location.longitude
        val latitude = location.latitude
        val geoCoder = Geocoder(appContext, Locale.getDefault())
        try {
            val addresses = geoCoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.size > 0) {
                return addresses[0]
            }
        } catch (e: IOException) {
            Log.e(LocationChangeChecker::class.java.simpleName, e.toString())
        }
        return null
    }

    private fun sendIntentToWidgetUpdate() {
        WidgetProvider.updateWidgetPendingIntent(appContext)
        WeatherUpdateBroadcastReceiver.updateWeatherPendingIntent(appContext)
    }
}