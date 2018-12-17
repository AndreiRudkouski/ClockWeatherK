package by.rudkouski.clockWeatherK.view.forecast

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
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import by.rudkouski.clockWeatherK.R
import by.rudkouski.clockWeatherK.database.DBHelper.Companion.INSTANCE
import by.rudkouski.clockWeatherK.provider.WidgetProvider
import by.rudkouski.clockWeatherK.view.location.LocationActivity
import by.rudkouski.clockWeatherK.view.weather.WeatherItemView

class ForecastActivity : AppCompatActivity() {

    private val dbHelper = INSTANCE
    private var activityUpdateBroadcastReceiver: ForecastActivityUpdateBroadcastReceiver? = null
    private var widgetId: Int = 0

    companion object {
        private const val FORECAST_ACTIVITY_UPDATE = "by.rudkouski.clockWeatherK.widget.FORECAST_ACTIVITY_UPDATE"

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
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            val widget = dbHelper.getWidgetById(widgetId)
            val title = widget.location.name
            toolbarLayout.title = title
            val weather = dbHelper.getWeatherByLocationId(widget.location.id)
            weatherView.updateWeatherItemView(weather)
        }
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
                WidgetProvider.updateWidgetPendingIntent(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        updateActivity()
        activityUpdateBroadcastReceiver = ForecastActivityUpdateBroadcastReceiver()
        registerReceiver(activityUpdateBroadcastReceiver, IntentFilter(FORECAST_ACTIVITY_UPDATE))
    }

    override fun onPause() {
        super.onPause()
        if (activityUpdateBroadcastReceiver != null) {
            unregisterReceiver(activityUpdateBroadcastReceiver)
        }
    }

    override fun onStop() {
        super.onStop()
        finish()
    }

    private inner class ForecastActivityUpdateBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateActivity()
        }
    }
}