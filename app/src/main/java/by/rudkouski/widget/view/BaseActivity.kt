package by.rudkouski.widget.view

import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import by.rudkouski.widget.R
import by.rudkouski.widget.entity.Setting
import by.rudkouski.widget.repository.SettingRepository.getSettingsByWidgetId
import by.rudkouski.widget.view.location.LocationActivity
import by.rudkouski.widget.view.location.LocationActivity.Companion.startLocationActivityIntent
import by.rudkouski.widget.view.setting.SettingActivity
import by.rudkouski.widget.view.setting.SettingActivity.Companion.startSettingActivityIntent

abstract class BaseActivity : AppCompatActivity() {

    protected var widgetId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        widgetId = intent?.extras?.getInt(EXTRA_APPWIDGET_ID) ?: 0
        changeWidgetTheme()
        super.onCreate(savedInstanceState)
    }

    private fun changeWidgetTheme() {
        val settings = getSettingsByWidgetId(widgetId)
        if (!settings.isNullOrEmpty()) {
            val isBlackTheme = settings.find { Setting.Code.SETTING_THEME == it.code }!!.getBooleanValue()
            val themeId = if (isBlackTheme) resources.getIdentifier("DarkTheme", "style", packageName) else
                resources.getIdentifier("LightTheme", "style", packageName)
            applicationInfo.theme = themeId
            setTheme(themeId)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.change_location_menu -> {
                if (this.componentName.className != LocationActivity::class.java.name) {
                    val intent = startLocationActivityIntent(this, widgetId)
                    startActivity(intent)
                }
                return true
            }
            R.id.setting_menu -> {
                if (this.componentName.className != SettingActivity::class.java.name) {
                    if (!getSettingsByWidgetId(widgetId).isNullOrEmpty()) {
                        val intent = startSettingActivityIntent(this, widgetId)
                        startActivity(intent)
                    }
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}