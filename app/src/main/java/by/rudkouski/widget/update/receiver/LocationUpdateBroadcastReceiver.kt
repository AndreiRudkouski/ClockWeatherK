package by.rudkouski.widget.update.receiver

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getBroadcast
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.location.*
import android.location.LocationManager.GPS_PROVIDER
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat.checkSelfPermission
import by.rudkouski.widget.app.App.Companion.appContext
import by.rudkouski.widget.entity.Location.Companion.CURRENT_LOCATION_ID
import by.rudkouski.widget.provider.WidgetProvider.Companion.updateWidget
import by.rudkouski.widget.repository.LocationRepository.getLocationById
import by.rudkouski.widget.repository.LocationRepository.resetCurrentLocation
import by.rudkouski.widget.repository.LocationRepository.updateCurrentLocationData
import by.rudkouski.widget.update.receiver.NetworkChangeChecker.isOnline
import by.rudkouski.widget.update.receiver.NetworkChangeChecker.registerReceiver
import by.rudkouski.widget.update.receiver.WeatherUpdateBroadcastReceiver.Companion.updateCurrentWeather
import by.rudkouski.widget.view.location.LocationActivity.Companion.updateLocationActivityBroadcast
import java.io.IOException

@SuppressLint("MissingPermission")
class LocationUpdateBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val LOCATION_UPDATE_REQUEST_CODE = 1004
        private const val LOCATION_UPDATE_ACTION = "by.rudkouski.widget.LOCATION_UPDATE"

        private val locationManager = appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        private val locationChangeListener = object : LocationListener {
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

            override fun onProviderEnabled(provider: String?) {}

            override fun onProviderDisabled(provider: String?) {}

            override fun onLocationChanged(location: Location?) {
                if (location != null) {
                    setLocation(location)
                    locationManager.removeUpdates(this)
                }
            }
        }

        fun getLocationUpdatePendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, LocationUpdateBroadcastReceiver::class.java)
            intent.action = LOCATION_UPDATE_ACTION
            return getBroadcast(context, LOCATION_UPDATE_REQUEST_CODE, intent, FLAG_UPDATE_CURRENT)
        }

        fun updateCurrentLocation(context: Context) {
            getLocationUpdatePendingIntent(context).send()
        }

        fun isPermissionsGranted() = !isPermissionsDenied()

        fun isPermissionsDenied(): Boolean {
            return checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_DENIED
                || checkSelfPermission(appContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_DENIED
        }

        fun isLocationEnabled(): Boolean {
            return locationManager.isProviderEnabled(GPS_PROVIDER)
        }

        private fun requestLocationUpdates(provider: String) {
            locationManager.requestLocationUpdates(provider, 0, 0f, locationChangeListener)
        }

        fun setCurrentLocation() {
            if (isPermissionsGranted()) {
                val location = locationManager.getLastKnownLocation(getProviderName())
                if (location != null) {
                    setLocation(location)
                } else {
                    requestLocationUpdates(GPS_PROVIDER)
                }
            }
        }

        private fun getProviderName(): String {
            val criteria = Criteria()
            criteria.powerRequirement = Criteria.POWER_LOW
            criteria.accuracy = Criteria.ACCURACY_FINE
            criteria.isSpeedRequired = true
            return locationManager.getBestProvider(criteria, true) ?: GPS_PROVIDER
        }

        private fun setLocation(lastLocation: Location) {
            val address = getAddress(lastLocation)
            if (address != null && (address.locality != null || address.subAdminArea != null || address.adminArea != null)) {
                val locationName =
                    when {
                        address.locality != null -> address.locality
                        address.subAdminArea != null -> address.subAdminArea
                        else -> address.adminArea
                    }
                val savedLocation = getLocationById(CURRENT_LOCATION_ID)
                if (savedLocation.name_code != locationName) {
                    updateCurrentLocationData(locationName, lastLocation.latitude, lastLocation.longitude)
                    sendIntentToWidgetUpdate()
                }
            }
        }

        private fun getAddress(location: Location): Address? {
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
                    Log.e(LocationUpdateBroadcastReceiver::class.java.simpleName, e.toString())
                }
            } else {
                registerReceiver()
            }
            return null
        }

        private fun sendIntentToWidgetUpdate() {
            updateLocationActivityBroadcast(appContext)
            updateWidget(appContext)
            updateCurrentWeather(appContext)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (LOCATION_UPDATE_ACTION == intent.action) {
            if (isPermissionsGranted()) {
                requestLocationUpdates(getProviderName())
            } else {
                resetCurrentLocation()
            }
        }
    }
}