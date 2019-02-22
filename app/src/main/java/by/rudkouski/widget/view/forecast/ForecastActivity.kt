package by.rudkouski.widget.view.forecast

import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.format.DateUtils.DAY_IN_MILLIS
import android.view.Menu
import android.view.MenuItem
import by.rudkouski.widget.R
import by.rudkouski.widget.database.DBHelper.Companion.INSTANCE
import by.rudkouski.widget.entity.Weather
import by.rudkouski.widget.entity.Widget
import by.rudkouski.widget.provider.WidgetProvider
import by.rudkouski.widget.view.location.LocationActivity
import by.rudkouski.widget.view.weather.HourWeatherAdapter
import by.rudkouski.widget.view.weather.WeatherItemView
import java.util.*
import kotlin.collections.ArrayList

class ForecastActivity : AppCompatActivity() {

    private val dbHelper = INSTANCE
    private var activityUpdateBroadcastReceiver: ForecastActivityUpdateBroadcastReceiver? = null
    private var widgetId: Int = 0
    private val hourWeathers = ArrayList<Weather>()

    companion object {
        private const val FORECAST_ACTIVITY_UPDATE = "by.rudkouski.widget.FORECAST_ACTIVITY_UPDATE"

        fun startIntent(context: Context, widgetId: Int): Intent {
            val intent = Intent(context, ForecastActivity::class.java)
            intent.putExtra(EXTRA_APPWIDGET_ID, widgetId)
            return intent
        }

        fun updateActivityBroadcast(context: Context) {
            val intent = Intent(FORECAST_ACTIVITY_UPDATE)
            context.sendBroadcast(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forecast_activity)
        widgetId = getAppWidgetId()
        val toolbar = findViewById<Toolbar>(R.id.toolbar_forecast)
        setSupportActionBar(toolbar)
        activityUpdateBroadcastReceiver = ForecastActivityUpdateBroadcastReceiver()
        registerReceiver(activityUpdateBroadcastReceiver, IntentFilter(FORECAST_ACTIVITY_UPDATE))
        updateActivity()
    }

    private fun getAppWidgetId() = getAppWidgetIdFromBundle(intent.extras)

    private fun getAppWidgetIdFromBundle(bundle: Bundle?): Int {
        return bundle?.getInt(EXTRA_APPWIDGET_ID) ?: Integer.MIN_VALUE
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
        val adapter = HourWeatherAdapter(hourWeathers)
        hourWeatherRecycler.adapter = adapter
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            val widget = dbHelper.getWidgetById(widgetId)
            if (widget != null) {
                val title = widget.location.name
                toolbarLayout.title = title
                val weather = dbHelper.getWeatherByLocationId(widget.location.id)
                weatherView.updateWeatherItemView(weather)
                hourWeatherViewUpdate(widget, adapter)
            }
        }
    }

    private fun hourWeatherViewUpdate(widget: Widget, adapter: HourWeatherAdapter) {
        if (hourWeathers.isNotEmpty()) hourWeathers.clear()
        hourWeathers.addAll(checkWeatherTime(dbHelper.getHourWeathersByLocationId(widget.location.id)))
        adapter.notifyDataSetChanged()
    }

    private fun checkWeatherTime(weathers: List<Weather>?): List<Weather> {
        val correctWeathers = ArrayList<Weather>()
        if (weathers != null) {
            for (weather in weathers) {
                if (isWeatherTimeCorrect(weather.date)) {
                    correctWeathers.add(weather)
                }
            }
        }
        return correctWeathers
    }

    private fun isWeatherTimeCorrect(time: Date): Boolean {
        val weatherTime = Calendar.getInstance()
        weatherTime.time = time
        val currentTime = Calendar.getInstance()
        return weatherTime.time.time - currentTime.time.time in 0..DAY_IN_MILLIS
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val menuItem = item.itemId
        when (menuItem) {
            R.id.change_location_menu -> {
                val intent = LocationActivity.startIntent(this, widgetId)
                startActivity(intent)
                return true
            }
            R.id.change_text_menu -> {
                val handler = Handler(Looper.getMainLooper())
                handler.post { dbHelper.changeWidgetTextBold(widgetId) }
                WidgetProvider.updateWidget(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()
        if (activityUpdateBroadcastReceiver != null) {
            unregisterReceiver(activityUpdateBroadcastReceiver)
        }
        WidgetProvider.updateWidget(this)
        finish()
    }

    private inner class ForecastActivityUpdateBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateActivity()
        }
    }
}