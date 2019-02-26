package by.rudkouski.widget.view

import android.appwidget.AppWidgetManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import by.rudkouski.widget.database.DBHelper
import by.rudkouski.widget.view.forecast.DayForecastActivity

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
}