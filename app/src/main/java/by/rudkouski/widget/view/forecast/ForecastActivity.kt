package by.rudkouski.widget.view.forecast

import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Bundle
import android.text.format.DateUtils.DAY_IN_MILLIS
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.rudkouski.widget.R
import by.rudkouski.widget.entity.Location
import by.rudkouski.widget.entity.Weather
import by.rudkouski.widget.entity.Widget
import by.rudkouski.widget.message.Message.showNetworkAndLocationEnableMessage
import by.rudkouski.widget.provider.WidgetProvider
import by.rudkouski.widget.repository.LocationRepository.getLocationById
import by.rudkouski.widget.repository.WeatherRepository.getCurrentWeatherByLocationId
import by.rudkouski.widget.repository.WeatherRepository.getHourWeathersByLocationId
import by.rudkouski.widget.repository.WidgetRepository.getWidgetById
import by.rudkouski.widget.view.BaseActivity
import by.rudkouski.widget.view.weather.HourWeatherAdapter
import by.rudkouski.widget.view.weather.WeatherItemView
import com.google.android.material.appbar.CollapsingToolbarLayout
import java.util.*
import kotlin.collections.ArrayList

class ForecastActivity : BaseActivity() {

    private lateinit var activityUpdateBroadcastReceiver: ForecastActivityUpdateBroadcastReceiver

    companion object {
        private val forecastActivityUpdateAction = "${ForecastActivity::class.java.`package`}.FORECAST_ACTIVITY_UPDATE"

        fun startIntent(context: Context, widgetId: Int): Intent {
            val intent = Intent(context, ForecastActivity::class.java)

            intent.putExtra(EXTRA_APPWIDGET_ID, widgetId)
            return intent
        }

        fun updateActivityBroadcast(context: Context) {
            val intent = Intent(forecastActivityUpdateAction)
            context.sendBroadcast(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forecast_activity)
        val toolbar = findViewById<Toolbar>(R.id.toolbar_forecast)
        setSupportActionBar(toolbar)
        activityUpdateBroadcastReceiver = ForecastActivityUpdateBroadcastReceiver()
        registerReceiver(activityUpdateBroadcastReceiver, IntentFilter(forecastActivityUpdateAction))
        updateActivity()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        initToolbar(widgetId)
    }

    private fun updateActivity() {
        initToolbar(widgetId)
        val manager = supportFragmentManager
        manager.beginTransaction()
            .replace(R.id.forecast_container, ForecastFragment.newInstance(widgetId), ForecastFragment::class.java.name)
            .commit()
    }

    private fun initToolbar(widgetId: Int) {
        val toolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar_forecast)
        val weatherView = findViewById<WeatherItemView>(R.id.current_weather)
        val hourWeatherRecycler = findViewById<RecyclerView>(R.id.hour_weather_recycler_view)
        hourWeatherRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val hourWeathers = ArrayList<Weather>()
        val adapter = HourWeatherAdapter(widgetId, hourWeathers)
        hourWeatherRecycler.adapter = adapter
        val widget = getWidgetById(widgetId)
        if (widget != null) {
            val title = getLocationById(widget.locationId).getName()
            toolbarLayout.title = title
            val weather = getCurrentWeatherByLocationId(widget.locationId)
            weatherView.updateWeatherItemView(weather)
            hourWeatherViewUpdate(widget, adapter, hourWeathers)
            showNetworkAndLocationEnableMessage(weatherView, widget.locationId, this)
        }
    }

    private fun hourWeatherViewUpdate(widget: Widget, adapter: HourWeatherAdapter, hourWeathers: ArrayList<Weather>) {
        if (hourWeathers.isNotEmpty()) hourWeathers.clear()
        val location = getLocationById(widget.locationId)
        hourWeathers.addAll(checkWeatherTime(location, getHourWeathersByLocationId(widget.locationId)))
        adapter.notifyDataSetChanged()
    }

    private fun checkWeatherTime(location: Location, weathers: List<Weather>?): List<Weather> {
        val correctWeathers = ArrayList<Weather>()
        if (weathers != null) {
            for (weather in weathers) {
                if (isWeatherTimeCorrect(weather.date, location.timeZone)) {
                    correctWeathers.add(weather)
                }
            }
        }
        return correctWeathers
    }

    private fun isWeatherTimeCorrect(weatherTime: Calendar, locationTimeZone: TimeZone): Boolean {
        val currentTime = Calendar.getInstance(locationTimeZone)
        return weatherTime.time.time - currentTime.time.time in 0..DAY_IN_MILLIS
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(activityUpdateBroadcastReceiver)
        WidgetProvider.updateWidget(this)
        finish()
    }

    private inner class ForecastActivityUpdateBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateActivity()
        }
    }
}