package by.rudkouski.widget.listener

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.AlarmManager.INTERVAL_FIFTEEN_MINUTES
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Address
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import by.rudkouski.widget.app.App.Companion.appContext
import by.rudkouski.widget.database.DBHelper.Companion.INSTANCE
import by.rudkouski.widget.entity.Location.Companion.CURRENT_LOCATION_ID
import by.rudkouski.widget.provider.WidgetProvider
import by.rudkouski.widget.receiver.WeatherUpdateBroadcastReceiver
import java.io.IOException
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean


@SuppressLint("MissingPermission")
object LocationChangeListener : LocationListener {

    private val dbHelper = INSTANCE
    private val isRegistered = AtomicBoolean(false)
    private val locationManager = appContext.getSystemService(LOCATION_SERVICE) as LocationManager

    override fun onLocationChanged(lastLocation: android.location.Location?) {
        if (lastLocation != null) {
            if (dbHelper.isCurrentLocationNotUpdated()) {
                locationManager.removeUpdates(this)
                setRequestLocationUpdates(INTERVAL_FIFTEEN_MINUTES)
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

    fun startLocationUpdate() {
        if (isPermissionsGranted()) {
            if (dbHelper.isCurrentLocationNotUpdated()) {
                if (isLocationEnabled()) {
                    val location = chooseLocation()
                    if (location != null) setLocation(location)
                } else {
                    setRequestLocationUpdates(0)
                    return
                }
            }
            setRequestLocationUpdates(INTERVAL_FIFTEEN_MINUTES)
        }
    }

    fun isLocationEnabled(): Boolean {
        val gpsEnabled = locationManager.isProviderEnabled(GPS_PROVIDER)
        val networkEnabled = locationManager.isProviderEnabled(NETWORK_PROVIDER)
        return gpsEnabled || networkEnabled
    }

    private fun chooseLocation(): android.location.Location? {
        val gpsLocation = locationManager.getLastKnownLocation(GPS_PROVIDER)
        val networkLocation = locationManager.getLastKnownLocation(NETWORK_PROVIDER)
        if (gpsLocation == null && networkLocation == null) return null
        if (gpsLocation != null && networkLocation == null) return gpsLocation
        if (networkLocation != null && gpsLocation == null) return networkLocation
        return if (gpsLocation.time >= networkLocation.time) gpsLocation else networkLocation
    }


    private fun setRequestLocationUpdates(period: Long) {
        locationManager.requestLocationUpdates(NETWORK_PROVIDER, period, 0f, this)
        locationManager.requestLocationUpdates(GPS_PROVIDER, period, 0f, this)
    }

    fun stopLocationUpdate() {
        if (isRegistered.get()) {
            locationManager.removeUpdates(this)
            isRegistered.set(false)
        }
    }

    private fun isPermissionsGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(appContext, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(appContext, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
    }

    fun isPermissionsDenied(): Boolean {
        return ActivityCompat.checkSelfPermission(appContext, ACCESS_FINE_LOCATION) == PERMISSION_DENIED
            || ActivityCompat.checkSelfPermission(appContext, ACCESS_COARSE_LOCATION) == PERMISSION_DENIED
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
            val needUpdate = locationName != dbHelper.getLocationById(CURRENT_LOCATION_ID).name
            dbHelper.updateCurrentLocation(locationName, address.latitude, address.longitude)
            if (needUpdate) {
                dbHelper.deleteWeatherForLocation(CURRENT_LOCATION_ID)
                sendIntentToWidgetUpdate()
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
            Log.e(LocationChangeListener::class.java.simpleName, e.toString())
        }
        return null
    }

    private fun sendIntentToWidgetUpdate() {
        WidgetProvider.updateWidget(appContext)
        WeatherUpdateBroadcastReceiver.updateWeather(appContext)
    }

    fun updateLocation() {
        if (isPermissionsGranted()) {
            val location = chooseLocation()
            if (location != null) setLocation(location)
        }
    }
}