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
import android.location.LocationManager.PASSIVE_PROVIDER
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat.checkSelfPermission
import by.rudkouski.widget.app.App.Companion.appContext
import by.rudkouski.widget.provider.WidgetProvider.Companion.updateWidget
import by.rudkouski.widget.repository.LocationRepository.resetCurrentLocation
import by.rudkouski.widget.repository.LocationRepository.updateCurrentLocationData
import by.rudkouski.widget.update.receiver.NetworkChangeChecker.isOnline
import by.rudkouski.widget.update.receiver.NetworkChangeChecker.registerNetworkChangeReceiver
import by.rudkouski.widget.update.receiver.WeatherUpdateBroadcastReceiver.Companion.updateCurrentWeather
import by.rudkouski.widget.update.scheduler.UpdateWeatherScheduler.LOCATION_UPDATE_INTERVAL_IN_MINUTES
import by.rudkouski.widget.view.location.LocationActivity.Companion.updateLocationActivityBroadcast
import org.threeten.bp.Instant.ofEpochMilli
import org.threeten.bp.OffsetDateTime.now
import org.threeten.bp.ZoneId.systemDefault
import java.io.IOException
import java.util.*
import java.util.Locale.getDefault

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

        fun isLocationEnabled() = isProviderEnable(GPS_PROVIDER)

        private fun isProviderEnable(name: String) = locationManager.isProviderEnabled(name)

        private fun requestLocationUpdate(provider: String) {
            locationManager.requestSingleUpdate(provider, locationChangeListener, Looper.getMainLooper())
        }

        fun setCurrentLocation() {
            if (isPermissionsGranted()) {
                updateLocation(getProviderName(), appContext, true)
            }
        }

        private fun updateLocation(name: String, context: Context, isRequestNeed: Boolean) {
            val location = getLocationByProviderName(name)
            if (isLocationApplicable(location)) {
                setLocation(location)
            } else {
                if (PASSIVE_PROVIDER == name) {
                    updateCurrentWeather(context)
                    if (isRequestNeed) {
                        requestLocationUpdate(GPS_PROVIDER)
                    }
                } else {
                    requestLocationUpdate(name)
                }
            }
        }

        private fun isLocationApplicable(location: Location?) =
            location != null &&
                ofEpochMilli(location.time).atZone(systemDefault()).toOffsetDateTime().plusMinutes(LOCATION_UPDATE_INTERVAL_IN_MINUTES).isAfter(now())

        private fun getProviderName(): String {
            val criteria = Criteria()
            criteria.powerRequirement = Criteria.POWER_LOW
            criteria.accuracy = Criteria.ACCURACY_FINE
            criteria.isSpeedRequired = true
            return locationManager.getBestProvider(criteria, true) ?: GPS_PROVIDER
        }

        private fun getLocationByProviderName(name: String) = locationManager.getLastKnownLocation(name)

        private fun setLocation(lastLocation: Location) {
            val address = getAddress(lastLocation)
            if (address != null && (address.locality != null || address.subAdminArea != null || address.adminArea != null)) {
                val locationName =
                    when {
                        address.locality != null -> address.locality
                        address.subAdminArea != null -> address.subAdminArea
                        else -> address.adminArea
                    }
                updateCurrentLocationData(locationName, lastLocation.latitude, lastLocation.longitude)
                sendUpdateIntents()
            } else {
                updateCurrentWeather(appContext)
            }
        }

        private fun getAddress(location: Location): Address? {
            if (isOnline()) {
                val longitude = location.longitude
                val latitude = location.latitude
                val geoCoder = Geocoder(appContext, getCurrentLocale())
                try {
                    val addresses = geoCoder.getFromLocation(latitude, longitude, 1)
                    if (addresses != null && addresses.size > 0) {
                        return addresses[0]
                    }
                } catch (e: IOException) {
                    Log.e(LocationUpdateBroadcastReceiver::class.java.simpleName, e.toString())
                }
            } else {
                registerNetworkChangeReceiver()
            }
            return null
        }

        private fun getCurrentLocale(): Locale {
            val locale = getDefault()
            if ("ru" == locale.language) return Locale("ru", "RU")
            return locale
        }

        private fun sendUpdateIntents() {
            updateLocationActivityBroadcast(appContext)
            updateWidget(appContext)
            updateCurrentWeather(appContext)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (LOCATION_UPDATE_ACTION == intent.action) {
            if (isPermissionsGranted()) {
                updateLocation(getProviderName(), context, false)
            } else {
                resetCurrentLocation()
            }
        }
    }
}