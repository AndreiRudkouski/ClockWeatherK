package by.rudkouski.widget.view.forecast

import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.rudkouski.widget.R
import by.rudkouski.widget.entity.Weather
import by.rudkouski.widget.message.Message.showNetworkAndLocationEnableMessage
import by.rudkouski.widget.provider.WidgetProvider.Companion.updateWidget
import by.rudkouski.widget.repository.LocationRepository.getLocationById
import by.rudkouski.widget.repository.WeatherRepository.getCurrentWeatherByLocationId
import by.rudkouski.widget.repository.WeatherRepository.getHourWeathersByLocationIdAndTimeInterval
import by.rudkouski.widget.repository.WidgetRepository.getWidgetById
import by.rudkouski.widget.view.BaseActivity
import by.rudkouski.widget.view.weather.HourWeatherAdapter
import by.rudkouski.widget.view.weather.WeatherItemView
import com.google.android.material.appbar.CollapsingToolbarLayout

class ForecastActivity : BaseActivity() {

    private lateinit var activityUpdateBroadcastReceiver: ForecastActivityUpdateBroadcastReceiver
    private val hourWeathers = ArrayList<Weather>()

    companion object {
        private const val FORECAST_ACTIVITY_UPDATE_ACTION = "by.rudkouski.widget.FORECAST_ACTIVITY_UPDATE"

        fun startIntent(context: Context, widgetId: Int): Intent {
            val intent = Intent(context, ForecastActivity::class.java)
            intent.putExtra(EXTRA_APPWIDGET_ID, widgetId)
            return intent
        }

        fun updateActivityBroadcast(context: Context) {
            val intent = Intent(FORECAST_ACTIVITY_UPDATE_ACTION)
            context.sendBroadcast(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forecast_activity)
        val toolbar = findViewById<Toolbar>(R.id.toolbar_forecast)
        setSupportActionBar(toolbar)
        activityUpdateBroadcastReceiver = ForecastActivityUpdateBroadcastReceiver()
        registerReceiver(activityUpdateBroadcastReceiver, IntentFilter(FORECAST_ACTIVITY_UPDATE_ACTION))
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
        val adapter = HourWeatherAdapter(this, widgetId, hourWeathers)
        hourWeatherRecycler.adapter = adapter
        val widget = getWidgetById(widgetId)
        if (widget != null) {
            val title = getLocationById(widget.locationId).getName(this)
            toolbarLayout.title = title
            val weather = getCurrentWeatherByLocationId(widget.locationId)
            weatherView.updateWeatherItemView(weather)
            hourWeatherViewUpdate(weather, adapter)
            showNetworkAndLocationEnableMessage(weatherView, widget.locationId, this)
        }
    }

    private fun hourWeatherViewUpdate(currentWeather: Weather?, adapter: HourWeatherAdapter) {
        if (currentWeather != null) {
            if (hourWeathers.isNotEmpty()) hourWeathers.clear()
            val weathers =
                getHourWeathersByLocationIdAndTimeInterval(currentWeather.locationId, currentWeather.date, currentWeather.date.plusHours(24))
            if (!weathers.isNullOrEmpty()) hourWeathers.addAll(weathers)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(activityUpdateBroadcastReceiver)
        updateWidget(this)
        finish()
    }

    private inner class ForecastActivityUpdateBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateActivity()
        }
    }
}