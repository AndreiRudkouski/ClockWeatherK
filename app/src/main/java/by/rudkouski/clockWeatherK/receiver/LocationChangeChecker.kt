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
import by.rudkouski.clockWeatherK.R
import by.rudkouski.clockWeatherK.app.App
import by.rudkouski.clockWeatherK.entity.Location
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import java.util.*

@SuppressLint("MissingPermission")
object LocationChangeChecker {

   var location = Location.createCurrentLocation(App.appContext.getString(R.string.default_location), 0.0, 0.0)

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            setLocation(locationResult.lastLocation)
        }
    }

    fun startLocationUpdate() {
        if (isPermissionsGranted()) {
            val locationRequest = LocationRequest()
            locationRequest.priority = PRIORITY_HIGH_ACCURACY
            locationRequest.interval = INTERVAL_FIFTEEN_MINUTES

            val locationSettingsRequest = LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()

            val settingsClient = LocationServices.getSettingsClient(App.appContext)
            settingsClient.checkLocationSettings(locationSettingsRequest)

            getFusedLocationProviderClient(App.appContext).requestLocationUpdates(locationRequest, locationCallback,
                Looper.myLooper())
        }
    }

    fun stopLocationUpdate() {
        getFusedLocationProviderClient(App.appContext).removeLocationUpdates(locationCallback)
    }

    fun isPermissionsGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(App.appContext, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(App.appContext, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
    }

    private fun setLocation(lastLocation: android.location.Location) {
        val address = getAddress(lastLocation)
        location = if (address != null) {
            val locationName =
                if (address.locality != null) address.locality else if (address.subAdminArea != null) address.subAdminArea else address.adminArea
            Location.createCurrentLocation(locationName, address.latitude, address.longitude)
        } else {
            Location.createCurrentLocation(App.appContext.getString(R.string.default_location), 0.0, 0.0)
        }
    }

    private fun getAddress(location: android.location.Location): Address? {
        val longitude = location.longitude
        val latitude = location.latitude
        val geoCoder = Geocoder(App.appContext, Locale.getDefault())
        val addresses = geoCoder.getFromLocation(latitude, longitude, 1)
        if (addresses != null && addresses.size > 0) {
            return addresses[0]
        }
        return null
    }
}