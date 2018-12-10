package by.rudkouski.clockWeatherK.receiver

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.AlarmManager.INTERVAL_FIFTEEN_MINUTES
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Address
import android.location.Geocoder
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.text.format.DateUtils.MINUTE_IN_MILLIS
import android.util.Log
import by.rudkouski.clockWeatherK.R
import by.rudkouski.clockWeatherK.app.App.Companion.appContext
import by.rudkouski.clockWeatherK.entity.Location
import by.rudkouski.clockWeatherK.provider.WidgetProvider
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import java.io.IOException
import java.util.*

@SuppressLint("MissingPermission")
object LocationChangeChecker {

    private var location: Location? = null

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            setLocation(locationResult.lastLocation)
        }
    }

    fun startLocationUpdate() {
        if (isPermissionsGranted()) {
            val locationRequest = createLocationRequest()
            checkLocationSetting(LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build())
            getFusedLocationProviderClient(appContext).requestLocationUpdates(locationRequest, locationCallback,
                Looper.getMainLooper())
        }
    }

    private fun createLocationRequest(): LocationRequest {
        val locationRequest = LocationRequest()
        locationRequest.priority = PRIORITY_HIGH_ACCURACY
        locationRequest.interval = INTERVAL_FIFTEEN_MINUTES
        locationRequest.fastestInterval = MINUTE_IN_MILLIS * 5
        return locationRequest
    }

    private fun checkLocationSetting(locationSettingsRequest: LocationSettingsRequest) {
        val settingsClient = LocationServices.getSettingsClient(appContext)
        settingsClient.checkLocationSettings(locationSettingsRequest)
    }

    fun stopLocationUpdate() {
        getFusedLocationProviderClient(appContext).removeLocationUpdates(locationCallback)
    }

    fun isPermissionsGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(appContext, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(appContext, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
    }

    fun getLastLocation(): Location {
        return if (location != null) location!!
        else Location.createCurrentLocation(appContext.getString(R.string.default_location), 0.0, 0.0)
    }

    private fun setLocation(lastLocation: android.location.Location) {
        val address = getAddress(lastLocation)
        val newLocation = if (address != null) {
            val locationName =
                if (address.locality != null) address.locality else if (address.subAdminArea != null) address.subAdminArea else address.adminArea
            Location.createCurrentLocation(locationName, address.latitude, address.longitude)
        } else {
            Location.createCurrentLocation(appContext.getString(R.string.default_location), lastLocation.latitude,
                lastLocation.longitude)
        }
        if (location == null) {
            location = newLocation
            sendIntentToWidgetUpdate()
        } else {
            location = newLocation
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