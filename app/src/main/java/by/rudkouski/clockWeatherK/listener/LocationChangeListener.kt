package by.rudkouski.clockWeatherK.listener

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
import by.rudkouski.clockWeatherK.app.App.Companion.appContext
import by.rudkouski.clockWeatherK.database.DBHelper
import by.rudkouski.clockWeatherK.provider.WidgetProvider
import by.rudkouski.clockWeatherK.receiver.WeatherUpdateBroadcastReceiver
import java.io.IOException
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean


@SuppressLint("MissingPermission")
object LocationChangeListener : LocationListener {

    private val dbHelper = DBHelper.INSTANCE
    private val isRegistered = AtomicBoolean(false)
    private val locationManager = appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

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
                setRequestLocationUpdates(0)
            } else {
                setRequestLocationUpdates(INTERVAL_FIFTEEN_MINUTES)
            }
        }
    }


    private fun setRequestLocationUpdates(period: Long) {
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, period, 0f, this)
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, period, 0f, this)
    }

    fun stopLocationUpdate() {
        if (isRegistered.get()) {
            locationManager.removeUpdates(this)
            isRegistered.set(false)
        }
    }

    fun isPermissionsGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(appContext, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(appContext, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
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
            if (dbHelper.isCurrentLocationNotUpdated()) {
                dbHelper.updateCurrentLocation(locationName, address.latitude, address.longitude)
                sendIntentToWidgetUpdate()
            } else {
                dbHelper.updateCurrentLocation(locationName, address.latitude, address.longitude)
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
}