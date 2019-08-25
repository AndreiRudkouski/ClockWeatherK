package by.rudkouski.widget.view

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import by.rudkouski.widget.R
import by.rudkouski.widget.provider.WidgetProvider
import by.rudkouski.widget.repository.WidgetRepository
import by.rudkouski.widget.repository.WidgetRepository.changeWidgetTextBold
import by.rudkouski.widget.repository.WidgetRepository.getWidgetById
import by.rudkouski.widget.view.location.LocationActivity

abstract class BaseActivity : AppCompatActivity() {

    protected var widgetId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        widgetId = intent?.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID) ?: 0
        changeWidgetTheme()
        super.onCreate(savedInstanceState)
    }

    private fun changeWidgetTheme() {
        val widget = getWidgetById(widgetId)
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
        when (item.itemId) {
            R.id.change_location_menu -> {
                val intent = LocationActivity.startIntent(this, widgetId)
                startActivity(intent)
                return true
            }
            R.id.change_text_menu -> {
                changeWidgetTextBold(widgetId)
                WidgetProvider.updateWidget(this)
                return true
            }
            R.id.change_theme_menu -> {
                val darkThemeId = resources.getIdentifier("DarkTheme", "style", packageName)
                val lightThemeId = resources.getIdentifier("LightTheme", "style", packageName)
                val themeId = if (applicationInfo.theme == darkThemeId) lightThemeId else darkThemeId
                WidgetRepository.changeWidgetTheme(widgetId, themeId)
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