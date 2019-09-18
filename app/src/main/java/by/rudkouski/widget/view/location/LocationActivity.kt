package by.rudkouski.widget.view.location

import android.Manifest
import android.app.Activity
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.view.View
import android.widget.ListView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import by.rudkouski.widget.R
import by.rudkouski.widget.app.Constants.LOCATION_ACTIVITY_UPDATE_WEATHER
import by.rudkouski.widget.app.Constants.REQUEST_PERMISSION_CODE
import by.rudkouski.widget.entity.Location.Companion.CURRENT_LOCATION_ID
import by.rudkouski.widget.message.Message.showNetworkAndLocationEnableMessage
import by.rudkouski.widget.provider.WidgetProvider.Companion.updateWidget
import by.rudkouski.widget.repository.LocationRepository.getAllLocations
import by.rudkouski.widget.repository.LocationRepository.getLocationByWidgetId
import by.rudkouski.widget.repository.LocationRepository.resetCurrentLocation
import by.rudkouski.widget.repository.WidgetRepository.setWidgetByIdAndLocationId
import by.rudkouski.widget.update.receiver.LocationUpdateBroadcastReceiver.Companion.isPermissionsDenied
import by.rudkouski.widget.update.receiver.LocationUpdateBroadcastReceiver.Companion.setCurrentLocation
import by.rudkouski.widget.update.receiver.WeatherUpdateBroadcastReceiver.Companion.updateOtherWeathers
import by.rudkouski.widget.view.BaseActivity

class LocationActivity : BaseActivity(), LocationsViewAdapter.OnLocationItemClickListener {

    private lateinit var activityUpdateBroadcastReceiver: LocationActivityUpdateBroadcastReceiver

    companion object {
        fun startLocationActivityIntent(context: Context, widgetId: Int): Intent {
            val intent = Intent(context, LocationActivity::class.java)
            intent.putExtra(EXTRA_APPWIDGET_ID, widgetId)
            return intent
        }

        fun updateLocationActivityBroadcast(context: Context) {
            val intent = Intent(LOCATION_ACTIVITY_UPDATE_WEATHER)
            context.sendBroadcast(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.location_activity)
        activityUpdateBroadcastReceiver = LocationActivityUpdateBroadcastReceiver()
        registerReceiver(activityUpdateBroadcastReceiver, IntentFilter(LOCATION_ACTIVITY_UPDATE_WEATHER))
        if (isPermissionsDenied()) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_PERMISSION_CODE)
        } else {
            initActivity()
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(activityUpdateBroadcastReceiver)
        finish()
    }

    private fun initActivity() {
        val toolbar: Toolbar = findViewById(R.id.toolbar_config)
        setSupportActionBar(toolbar)
        setResult(RESULT_CANCELED)
        setLocations()
    }

    private fun setLocations() {
        val locationsView: ListView = findViewById(R.id.locations_config)
        var locations = getAllLocations()
        if (isPermissionsDenied()) {
            locations = locations.filter { location -> location.id != CURRENT_LOCATION_ID }
        } else {
            showNetworkAndLocationEnableMessage(locationsView, CURRENT_LOCATION_ID, this)
        }
        locationsView.adapter = LocationsViewAdapter(this, this, locations, getSelectedLocationId())
    }

    private fun getSelectedLocationId(): Int = getLocationByWidgetId(widgetId)?.id ?: 0

    override fun onLocationItemClick(view: View, locationId: Int) {
        locationItemClickEvent(locationId)
    }

    private fun locationItemClickEvent(locationId: Int) {
        if (setWidgetByIdAndLocationId(widgetId, locationId)) {
            if (CURRENT_LOCATION_ID == locationId) {
                setCurrentLocation()
            } else {
                updateOtherWeathers(this)
            }
            updateWidget(this)
            setResultIntent()
        } else {
            finish()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                    setCurrentLocation()
                } else {
                    resetCurrentLocation()
                }
                initActivity()
            }
        }
    }

    private fun setResultIntent() {
        val result = Intent()
        result.putExtra(EXTRA_APPWIDGET_ID, widgetId)
        setResult(Activity.RESULT_OK, result)
        finish()
    }

    private inner class LocationActivityUpdateBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            initActivity()
        }
    }
}