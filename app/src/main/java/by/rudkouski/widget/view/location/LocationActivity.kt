package by.rudkouski.widget.view.location

import android.Manifest
import android.app.Activity
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.Toolbar
import android.text.SpannableString
import android.view.View
import android.widget.ListView
import by.rudkouski.widget.R
import by.rudkouski.widget.app.App
import by.rudkouski.widget.entity.Location.Companion.CURRENT_LOCATION_ID
import by.rudkouski.widget.listener.LocationChangeListener
import by.rudkouski.widget.message.Message
import by.rudkouski.widget.provider.WidgetProvider
import by.rudkouski.widget.receiver.WeatherUpdateBroadcastReceiver
import by.rudkouski.widget.view.BaseActivity

class LocationActivity : BaseActivity(), LocationsViewAdapter.OnLocationItemClickListener {

    companion object {
        private const val requestPermissionCode = 1234

        fun startIntent(context: Context, widgetId: Int): Intent {
            val intent = Intent(context, LocationActivity::class.java)
            intent.putExtra(EXTRA_APPWIDGET_ID, widgetId)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.location_activity)
        val toolbar: Toolbar = findViewById(R.id.toolbar_config)
        setSupportActionBar(toolbar)
        setResult(RESULT_CANCELED)
        val handler = Handler(Looper.getMainLooper())
        handler.post(this::setLocations)
    }

    override fun onStop() {
        super.onStop()
        finish()
    }

    private fun setLocations() {
        val locationsView = findViewById<ListView>(R.id.locations_config)
        locationsView.adapter = LocationsViewAdapter(this, this, dbHelper.getAllLocations(), getSelectedLocationId())
        checkLocationEnabled(locationsView)
    }

    private fun getSelectedLocationId(): Int = dbHelper.getLocationByWidgetId(widgetId)

    private fun checkLocationEnabled(view: View) {
        if (!LocationChangeListener.isLocationEnabled()) {
            val message = SpannableString(App.appContext.getString(R.string.no_location))
            Message.getShortMessage(message, view, this)
        }
    }

    override fun onLocationItemClick(view: View, locationId: Int) {
        val handler = Handler(Looper.getMainLooper())
        handler.post { locationItemClickEvent(locationId) }
    }

    private fun locationItemClickEvent(locationId: Int) {
        if (dbHelper.setWidgetById(widgetId, locationId)) {
            if (CURRENT_LOCATION_ID == locationId) {
                activitiesForCurrentLocation()
            } else {
                activitiesForNonCurrentLocation()
            }
        } else {
            finish()
        }
    }

    private fun activitiesForCurrentLocation() {
        if (LocationChangeListener.isPermissionsGranted()) {
            updateLocationAndWeather()
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                requestPermissionCode)
        }
    }

    private fun activitiesForNonCurrentLocation() {
        updateWidgetAndWeather()
        setResultIntent()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == requestPermissionCode) {
            for (permission in grantResults) {
                if (permission == PackageManager.PERMISSION_GRANTED) {
                    updateLocationAndWeather()
                    return
                }
            }
        }
    }

    private fun updateLocationAndWeather() {
        LocationChangeListener.startLocationUpdate()
        activitiesForNonCurrentLocation()
        finish()
    }

    private fun updateWidgetAndWeather() {
        WidgetProvider.updateWidget(this)
        WeatherUpdateBroadcastReceiver.updateWeather(this)
    }

    private fun setResultIntent() {
        val result = Intent()
        result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        setResult(Activity.RESULT_OK, result)
        finish()
    }
}