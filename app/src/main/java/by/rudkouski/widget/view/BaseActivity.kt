package by.rudkouski.widget.view

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import by.rudkouski.widget.R
import by.rudkouski.widget.database.DBHelper
import by.rudkouski.widget.provider.WidgetProvider
import by.rudkouski.widget.view.forecast.DayForecastActivity
import by.rudkouski.widget.view.location.LocationActivity

abstract class BaseActivity : AppCompatActivity() {

    protected val dbHelper = DBHelper.INSTANCE
    protected var widgetId = 0
    protected var forecastId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        widgetId = intent?.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID) ?: 0
        forecastId = intent?.extras?.getLong(DayForecastActivity.EXTRA_FORECAST_ID) ?: 0L
        if (widgetId == 0 && forecastId != 0L) {
            widgetId = dbHelper.getWidgetIdByForecastId(forecastId)
        }
        changeWidgetTheme()
        super.onCreate(savedInstanceState)
    }

    private fun changeWidgetTheme() {
        val widget = dbHelper.getWidgetById(widgetId)
        if (widget != null) {
            applicationInfo.theme = widget.themeId
            setTheme(widget.themeId)
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
                WidgetProvider.updateWidget(this)
                return true
            }
            R.id.change_theme_menu -> {
                val darkThemeId = resources.getIdentifier("DarkTheme", "style", packageName)
                val lightThemeId = resources.getIdentifier("LightTheme", "style", packageName)
                val themeId = if (applicationInfo.theme == darkThemeId) lightThemeId else darkThemeId
                val handler = Handler(Looper.getMainLooper())
                handler.post { dbHelper.changeWidgetTheme(widgetId, themeId) }
                WidgetProvider.updateWidget(this)
                finish()
                intent.flags = Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}