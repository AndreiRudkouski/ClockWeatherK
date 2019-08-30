package by.rudkouski.widget.update.listener

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.AlarmManager.INTERVAL_FIFTEEN_MINUTES
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.location.Address
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import by.rudkouski.widget.app.App.Companion.appContext
import by.rudkouski.widget.entity.Location.Companion.CURRENT_LOCATION_ID
import by.rudkouski.widget.provider.WidgetProvider.Companion.updateWidget
import by.rudkouski.widget.repository.LocationRepository.getLocationById
import by.rudkouski.widget.repository.LocationRepository.updateCurrentLocationData
import by.rudkouski.widget.update.receiver.NetworkChangeChecker.isOnline
import by.rudkouski.widget.update.receiver.NetworkChangeChecker.registerReceiver
import by.rudkouski.widget.update.receiver.WeatherUpdateBroadcastReceiver
import by.rudkouski.widget.view.location.LocationActivity
import java.io.IOException


@SuppressLint("MissingPermission")
object LocationChangeListener : LocationListener {

    private val locationManager = appContext.getSystemService(LOCATION_SERVICE) as LocationManager

    private const val LOCATION_UPDATE_INTERVAL = INTERVAL_FIFTEEN_MINUTES
    private const val LOCATION_UPDATE_DISTANCE = 0f

    override fun onLocationChanged(lastLocation: android.location.Location?) {
        if (lastLocation != null) {
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
            setLocation(chooseLocation())
            setRequestLocationUpdates()
        }
    }

    fun isLocationEnabled(): Boolean {
        val gpsEnabled = locationManager.isProviderEnabled(GPS_PROVIDER)
        val networkEnabled = locationManager.isProviderEnabled(NETWORK_PROVIDER)
        return gpsEnabled || networkEnabled
    }

    private fun chooseLocation(): android.location.Location? {
        if (isLocationEnabled()) {
            val gpsLocation = locationManager.getLastKnownLocation(GPS_PROVIDER)
            val networkLocation = locationManager.getLastKnownLocation(NETWORK_PROVIDER)
            if (gpsLocation == null && networkLocation == null) return null
            if (gpsLocation != null && networkLocation == null) return gpsLocation
            if (networkLocation != null && gpsLocation == null) return networkLocation
            return if (gpsLocation.time >= networkLocation.time) gpsLocation else networkLocation
        }
        return null
    }

    private fun setRequestLocationUpdates() {
        locationManager.requestLocationUpdates(NETWORK_PROVIDER, LOCATION_UPDATE_INTERVAL, LOCATION_UPDATE_DISTANCE, this)
        locationManager.requestLocationUpdates(GPS_PROVIDER, LOCATION_UPDATE_INTERVAL, LOCATION_UPDATE_DISTANCE, this)
    }

    fun stopLocationUpdate() {
        locationManager.removeUpdates(this)
    }

    private fun isPermissionsGranted() = !isPermissionsDenied()

    fun isPermissionsDenied(): Boolean {
        return ActivityCompat.checkSelfPermission(appContext, ACCESS_FINE_LOCATION) == PERMISSION_DENIED
            || ActivityCompat.checkSelfPermission(appContext, ACCESS_COARSE_LOCATION) == PERMISSION_DENIED
    }

    private fun setLocation(lastLocation: android.location.Location?) {
        if (lastLocation != null) {
            val address = getAddress(lastLocation)
            if (address != null && (address.locality != null || address.subAdminArea != null || address.adminArea != null)) {
                val locationName =
                    when {
                        address.locality != null -> address.locality
                        address.subAdminArea != null -> address.subAdminArea
                        else -> address.adminArea
                    }
                val savedLocation = getLocationById(CURRENT_LOCATION_ID)
                if (savedLocation.name_code != locationName || savedLocation.latitude != lastLocation.latitude || savedLocation.longitude != lastLocation.longitude) {
                    updateCurrentLocationData(locationName, lastLocation.latitude, lastLocation.longitude)
                    sendIntentToWidgetUpdate()
                }
            }

        }
    }

    private fun getAddress(location: android.location.Location): Address? {
        if (isOnline()) {
            val longitude = location.longitude
            val latitude = location.latitude
            val locale = appContext.resources.configuration.locales[0]
            val geoCoder = Geocoder(appContext, locale)
            try {
                val addresses = geoCoder.getFromLocation(latitude, longitude, 1)
                if (addresses != null && addresses.size > 0) {
                    return addresses[0]
                }
            } catch (e: IOException) {
                Log.e(LocationChangeListener::class.java.simpleName, e.toString())
            }
        } else {
            registerReceiver()
        }
        return null
    }

    private fun sendIntentToWidgetUpdate() {
        updateWidget(appContext)
        LocationActivity.updateActivityBroadcast(appContext)
        WeatherUpdateBroadcastReceiver.updateCurrentWeather(appContext)
    }

    fun updateLocation() {
        if (isPermissionsGranted()) {
            setLocation(chooseLocation())
        }
    }
}